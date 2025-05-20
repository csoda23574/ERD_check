package org.chefcrew.food.controller;

import lombok.RequiredArgsConstructor;
import org.chefcrew.food.dto.request.DeleteFoodRequest;
import org.chefcrew.food.dto.request.AddFoodRequest;
import org.chefcrew.food.dto.request.PostAmountUpdateRequest;
import org.chefcrew.food.dto.response.GetOwnFoodResponse;
import org.chefcrew.food.service.FoodService;
import org.chefcrew.jwt.UserId;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/food")
@RequiredArgsConstructor
public class FoodController {
    private final FoodService foodService;

    @PostMapping(value = "/amount-update", consumes = "application/json")
    public ResponseEntity<Map<String, Object>> updateFoodAmount(@UserId Long userId, @RequestBody PostAmountUpdateRequest requestBody) {
        foodService.updateFoodAmountAndUnit(userId, requestBody);
        Map<String, Object> result = new java.util.HashMap<>();
        result.put("success", true);
        return ResponseEntity.ok(result);
    }

    @PostMapping("")
    public ResponseEntity<Map<String, Object>> saveNewFoodList(@UserId Long userId, @RequestBody AddFoodRequest requestBody) {
        Map<String, Object> result = new java.util.HashMap<>();
        try {
            foodService.saveFoodList(userId, requestBody);
            // 식재료명 리스트 중 이미 존재하는 것이 있으면 안내 메시지 추가
            // (실제 누적 여부는 서비스에서 처리, 여기서는 안내만)
            result.put("success", true);
            result.put("message", "식재료가 성공적으로 추가되었습니다. 이미 등록된 식재료는 수량이 누적됩니다.");
            return ResponseEntity.ok(result);
        } catch (org.chefcrew.common.exception.CustomException ce) {
            result.put("success", false);
            result.put("error", ce.getErrorException().getErrorMessage());
            return ResponseEntity.badRequest().body(result);
        } catch (Exception e) {
            result.put("success", false);
            result.put("error", e.getMessage() != null ? e.getMessage() : "식재료 추가에 실패했습니다");
            return ResponseEntity.internalServerError().body(result);
        }
    }

    @GetMapping("/ownlist")
    private ResponseEntity<GetOwnFoodResponse> getOwnFoodList(@UserId Long userId) {
        return ResponseEntity.ok()
                .body(new GetOwnFoodResponse(foodService.getOwnedFoodList(userId)));
    }

    @DeleteMapping("")
    private ResponseEntity<Void> deleteUsedFood(@UserId Long userId, @RequestBody DeleteFoodRequest requestBody){
        foodService.deleteFood(userId, requestBody);
        return ResponseEntity.ok().build();
    }
}
