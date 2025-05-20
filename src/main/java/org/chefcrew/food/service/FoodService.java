package org.chefcrew.food.service;

import static org.chefcrew.common.exception.ErrorException.USER_NOT_FOUND;

import jakarta.transaction.Transactional;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.chefcrew.common.exception.CustomException;
import org.chefcrew.food.dto.request.AddFoodRequest;
import org.chefcrew.food.dto.request.DeleteFoodRequest;
import org.chefcrew.food.dto.response.FoodSimpleResponse;
import org.chefcrew.food.entity.Food;
import org.chefcrew.food.repository.FoodRepository;
import org.chefcrew.user.service.UserService;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class FoodService {
    public final FoodRepository foodRepository;
    public final UserService userService;

    // 식재료명에서 단위/수량/괄호/접두사 등 제거 (예: "●소스 :양파 100g(1/2개)" → "양파")
    private String extractPureName(String name) {
        if (name == null) return null;
        // ●, •, -, 소스, 양념, :, 공백 등 접두사 제거 + 괄호 및 그 안의 내용, 숫자+단위, 불필요한 공백 제거
        return name.replaceAll("^[●•\\-\\s]*(소스|양념)?\\s*:?", "")
                   .replaceAll("\\([^)]*\\)", "")
                   .replaceAll("\\s*\\d+[a-zA-Z가-힣()\\/\\.]*", "")
                   .trim();
    }

    @Transactional
    public void saveFoodList(long userId, AddFoodRequest foodAddRequest) {
        validateUser(userId);
        List<String> names = foodAddRequest.foodNameList();
        List<String> quantities = foodAddRequest.quantityList();
        List<String> units = foodAddRequest.unitList();
        List<Food> foodDataList = new java.util.ArrayList<>();
        for (int i = 0; i < names.size(); i++) {
            String name = extractPureName(names.get(i));
            if (name == null || name.isEmpty()) {
                System.out.println("[FoodService] 식재료명 파싱 결과가 비어있어 저장하지 않음: 원본=" + names.get(i));
                continue;
            }
            Float amount = (quantities != null && quantities.size() > i && quantities.get(i) != null && !quantities.get(i).isEmpty()) ? Float.parseFloat(quantities.get(i)) : 0f;
            String unit = (units != null && units.size() > i) ? units.get(i) : null;
            Food existing = foodRepository.findByFoodNameAndUserId(name, userId);
            if (existing == null) {
                // 신규 추가 (setter 방식)
                Food food = new Food();
                food.setFoodName(name);
                food.setUserId(userId);
                food.setAmount(amount);
                food.setUnit(unit);
                foodDataList.add(food);
            } else {
                // 이미 있으면 양을 더해서 저장
                existing.setAmount(existing.getAmount() + amount);
                if (unit != null && !unit.isEmpty()) {
                    existing.setUnit(unit);
                }
                foodRepository.saveFood(existing);
            }
        }
        if (!foodDataList.isEmpty()) {
            foodDataList.forEach(foodRepository::saveFood);
        }
    }

    @Transactional
    public void updateFoodAmountAndUnit(long userId, org.chefcrew.food.dto.request.PostAmountUpdateRequest request) {
        // userId가 -1로 들어오면 현재 로그인한 유저의 id로 대체
        if (userId == -1) {
            Long currentUserId = userService.getCurrentUserId(); // 현재 로그인한 유저의 id를 반환하는 메서드가 필요
            if (currentUserId == null) {
                throw new CustomException(USER_NOT_FOUND);
            }
            System.out.println("[FoodService] userId가 -1로 들어와 현재 로그인 유저 id로 대체: " + currentUserId);
            userId = currentUserId;
        }
        // userId가 0 이하(비정상)면 동작하지 않음
        if (userId <= 0) {
            System.out.println("[FoodService] 잘못된 userId: " + userId);
            return;
        }
        if (request.foodDataList() == null) return;
        for (var foodData : request.foodDataList()) {
            String pureName = extractPureName(foodData.getFoodName());
            System.out.println("[FoodService] 수신 foodData: foodName=" + foodData.getFoodName() + ", pureName=" + pureName + ", amount=" + foodData.getAmount() + ", userId=" + userId);
            Food food = foodRepository.findByFoodNameAndUserId(pureName, userId);
            System.out.println("[FoodService] DB 조회: SELECT * FROM food WHERE food_name='" + pureName + "' AND user_id=" + userId);
            System.out.println("[FoodService] DB 조회 결과: " + (food != null ? (food.getFoodName() + ", id=" + food.getFoodId() + ", userId=" + food.getUserId()) : "null"));
            if (food != null) {
                System.out.println("[FoodService] 검사: 요청 userId=" + userId + ", food.userId=" + food.getUserId());
                float newAmount = food.getAmount() - foodData.getAmount();
                if (newAmount < 0) newAmount = 0;
                food.setAmount(newAmount);
                System.out.println("UPDATE food SET quantity = quantity - " + foodData.getAmount() + " FROM food WHERE food_name='" + pureName + "' AND user_id=" + userId + ";");
                foodRepository.saveFood(food);
            }
        }
    }

    private void validateUser(long userId) {
        if (userService.getUserInfo(userId) == null) {
            throw new CustomException(USER_NOT_FOUND);
        }
    }

    public List<FoodSimpleResponse> getOwnedFoodList(long userId) {
        validateUser(userId);
        List<Food> foodList = foodRepository.findByUserId(userId);
        if (foodList == null || foodList.isEmpty()) {
            return java.util.Collections.emptyList();
        }
        return foodList.stream()
                .map(food -> new FoodSimpleResponse(
                        food.getFoodId(),
                        food.getFoodName(),
                        String.valueOf(food.getAmount()),
                        food.getUnit() // unit 필드 추가
                ))
                .collect(java.util.stream.Collectors.toList());
    }

    public void deleteFood(long userId, DeleteFoodRequest deleteFoodRequest) {
        foodRepository.deleteFood(userId, deleteFoodRequest.foodNameList());
    }
}
