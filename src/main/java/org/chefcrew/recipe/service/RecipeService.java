package org.chefcrew.recipe.service;

import static org.chefcrew.common.exception.ErrorException.OPEN_API_SERVER_ERROR;

// import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j; // Slf4j import 추가
import org.chefcrew.common.exception.CustomException;
import org.chefcrew.recipe.domain.Recipe;
import org.chefcrew.recipe.dto.request.GetRecipeRequest;
import org.chefcrew.recipe.dto.response.GetRecipeOpenResponse;
import org.chefcrew.recipe.dto.response.GetRecipeOpenResponse.RecipeData;
import org.chefcrew.recipe.dto.response.GetRecipeResponse;
import org.chefcrew.recipe.enums.ValueOption;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j // Slf4j 어노테이션 추가
public class RecipeService {

    @Value("${apiKey}")
    private String apiKey;

    // private final ObjectMapper objectMapper = new ObjectMapper(); // JSON 로깅을 위한 ObjectMapper

    public GetRecipeResponse getRecommendRecipe(GetRecipeRequest getRecipeRequest, boolean exactMatch) {

        Boolean calorieHigh = null;
        Boolean natriumHigh = null;
        Boolean fatHigh = null;
        Boolean protienHigh = null;
        Boolean carbohydrateHigh = null;

        int startIndex = getRecipeRequest.startIndex() != null ? getRecipeRequest.startIndex() : 1;
        int endIndex = getRecipeRequest.endIndex() != null ? getRecipeRequest.endIndex() : 15;
        //공공데이터에 메뉴 조회
        // ingredients가 있으면 join해서 API에 넘기고, 없으면 foodName 사용
        // 1. 넓은 범위로 충분히 많은 레시피를 받아온다 (예: 1~200)
        int apiStartIndex = 1;
        int apiEndIndex = 1000; // 충분히 넓게 받아오기
        String ingredientParam = null;
        if (getRecipeRequest.ingredients() != null && !getRecipeRequest.ingredients().isEmpty()) {
            ingredientParam = String.join(",", getRecipeRequest.ingredients());
        } else {
            ingredientParam = getRecipeRequest.foodName();
        }
        GetRecipeOpenResponse getRecipeOpenResponse = getMenuDataFromApi(ingredientParam, apiStartIndex, apiEndIndex);
        
        // 추가된 로그: getMenuDataFromApi 응답 로깅
        // try {
        //     log.info("[RecipeService] getRecommendRecipe - getMenuDataFromApi 응답: {}", objectMapper.writeValueAsString(getRecipeOpenResponse));
        // } catch (JsonProcessingException e) {
        //     log.warn("[RecipeService] getRecommendRecipe - getMenuDataFromApi 응답 JSON 변환 실패", e);
        //     log.info("[RecipeService] getRecommendRecipe - getMenuDataFromApi 응답 (raw): {}", getRecipeOpenResponse);
        // }

        //메뉴 필터링
        calorieHigh = getRecipeRequest.calorie() == ValueOption.NONE ? null
                : (getRecipeRequest.calorie() == ValueOption.HIGH ? true : false);        //700kcal 이상
        fatHigh = getRecipeRequest.fat() == ValueOption.NONE ? null
                : (getRecipeRequest.fat() == ValueOption.HIGH ? true : false);         //지방

        natriumHigh = getRecipeRequest.natrium() == ValueOption.NONE ? null
                : (getRecipeRequest.natrium() == ValueOption.HIGH ? true : false);         //나트륨
        protienHigh = getRecipeRequest.protien() == ValueOption.NONE ? null
                : (getRecipeRequest.protien() == ValueOption.HIGH ? true : false);         //단백질
        carbohydrateHigh = getRecipeRequest.carbohydrate() == ValueOption.NONE ? null
                : (getRecipeRequest.carbohydrate() == ValueOption.HIGH ? true : false);         //탄수화물

        //mapping해서 전달
        Boolean finalCalorieHigh = calorieHigh;
        Boolean finalFatHigh = fatHigh;
        Boolean finalNatriumHigh = natriumHigh;
        Boolean finalProtienHigh = protienHigh;
        Boolean finalCarbohydrateHigh = carbohydrateHigh;
        if(getRecipeOpenResponse.cookRcpInfo().row() == null)
            throw new CustomException(OPEN_API_SERVER_ERROR);
        List<RecipeData> recipeResponseList = getRecipeOpenResponse.cookRcpInfo().row()
                .stream()
                .filter(recipeData -> (isAppriateRecipe(finalCalorieHigh, finalFatHigh, finalNatriumHigh,
                        finalProtienHigh,
                        finalCarbohydrateHigh, recipeData)))
                .filter(recipeData -> {
                    List<String> searchIngredients = getRecipeRequest.ingredients();
                    String[] recipeIngredients = recipeData.partsDetails().split("\\n|, |,|\\n");
                    List<String> recipeIngredientList = Arrays.stream(recipeIngredients)
                        .map(food -> {
                            String[] parts = food.split(": ");
                            String raw = parts.length >= 2 ? parts[1] : food;
                            // 프리픽스(●, 소스, 양념, :, 공백 등)만 제거, 나머지는 그대로
                            return raw.replaceAll("^(●\\s*)?(소스|양념)?\\s*:?-?\\s*", "").trim();
                        })
                        .collect(Collectors.toList());
                    if (searchIngredients != null && !searchIngredients.isEmpty()) {
                        return searchIngredients.stream().allMatch(
                            search -> recipeIngredientList.stream().anyMatch(recipeIng -> recipeIng.contains(search))
                        );
                    }
                    if (getRecipeRequest.foodName() != null && !getRecipeRequest.foodName().isEmpty()) {
                        return recipeIngredientList.stream().anyMatch(recipeIng -> recipeIng.contains(getRecipeRequest.foodName()));
                    }
                    return true;
                })
                // 2. 필터링된 결과에서 사용자가 요청한 startIndex~endIndex만큼만 자르기
                .skip(startIndex - 1)
                .limit(endIndex - startIndex + 1)
                .collect(Collectors.toList());

        List<Recipe> recipeList = recipeResponseList.stream()
                .map(recipeData -> {
                    Recipe mappedRecipe = new Recipe(
                            recipeData.recipeName(),
                            Arrays.stream(recipeData.partsDetails().split("\\\n|, |,|\\\n") )
                                    .map(food -> {
                                        String[] parts = food.split(": ");
                                        String raw = parts.length >= 2 ? parts[1] : food;
                                        // 프리픽스(●, 소스, 양념, :, 공백 등)만 제거, 나머지는 그대로
                                        return raw.replaceAll("^(●\\s*)?(소스|양념)?\\s*:?-?\\s*", "").trim();
                                    })
                                    .toList(),
                            recipeData.getManuals(),
                            recipeData.getManualImages(),
                            recipeData.infoCal(),
                            recipeData.infoNa(),
                            recipeData.infoFat(),
                            recipeData.infoPro(),
                            recipeData.infoCar(),
                            recipeData.attFileNoMk(),
                            recipeData.attFileNoMain(),
                            recipeData.recipeSeq() // rcpSeq 추가
                    );
                    return mappedRecipe;
                })
                .collect(Collectors.toList());

        return new GetRecipeResponse(recipeList);
    }

    private boolean isAppriateRecipe(Boolean finalCalorieHigh, Boolean finalFatHigh, Boolean finalNatriumHigh,
                                     Boolean finalProtienHigh, Boolean finalCarbohydrateHigh, RecipeData recipeData) {
        return (finalCalorieHigh == null || finalCalorieHigh == isCalorieHigh(recipeData.infoCal()))
                && (finalFatHigh == null || finalFatHigh == isFatHigh(recipeData.infoFat()))
                && (finalNatriumHigh == null || finalNatriumHigh == isNatriumHigh(recipeData.infoNa()))
                && (finalProtienHigh == null || finalProtienHigh == isProtienHigh(recipeData.infoPro()))
                && (finalCarbohydrateHigh == null || finalCarbohydrateHigh == isCarbohydrateHigh(recipeData.infoCar()));
    }

    private boolean isCalorieHigh(float calorie) {
        return calorie > 700;
    }

    private boolean isFatHigh(float fat) {
        return fat > 10;
    }

    private boolean isNatriumHigh(float natrium) {
        return natrium > 700;
    }

    private boolean isProtienHigh(float protien) {
        return protien > 10;
    }

    private boolean isCarbohydrateHigh(float carbohydrate) {
        return carbohydrate > 100;
    }

    //공공데이터 서버에 재료 사용한 메뉴 정보 조회
    //open api 통신 과정
    public GetRecipeOpenResponse getMenuDataFromApi(String ingredient, int startIndex, int endIndex) {
        //서버랑 통신
        RestTemplate restTemplate = new RestTemplate();

        String apiURL = "http://openapi.foodsafetykorea.go.kr/api/"
                + apiKey
                + "/COOKRCP01"
                + "/json"
                + "/1/" + (endIndex+1)
                + "/RCP_PARTS_DTLS="
                + ingredient;
        System.out.println(apiURL);
        // HttpEntity 생성 시 MultiValueMap<String, String> 타입의 헤더를 명시적으로 전달해야 함
        final HttpEntity<String> entity = new HttpEntity<>(null, new org.springframework.http.HttpHeaders());

        GetRecipeOpenResponse response = restTemplate.exchange(apiURL, HttpMethod.GET, entity, GetRecipeOpenResponse.class)
                .getBody(); //여기서 바로 통신한 결과 리턴하는 형식
        
        // 추가된 로그: getMenuDataFromApi 실제 응답 로깅
        // try {
        //     log.info("[RecipeService] getMenuDataFromApi - 외부 API 응답 (ingredient: {}): {}", ingredient, objectMapper.writeValueAsString(response));
        // } catch (JsonProcessingException e) {
        //     log.warn("[RecipeService] getMenuDataFromApi - 외부 API 응답 JSON 변환 실패 (ingredient: {})", ingredient, e);
        //     log.info("[RecipeService] getMenuDataFromApi - 외부 API 응답 (raw, ingredient: {}): {}",ingredient, response);
        // }
        return response;
    }






    // RCP_SEQ로 특정 레시피 상세 정보 조회하는 메서드 추가 (RCP_SEQ는Restful 형식이라 작동x)

    public GetRecipeOpenResponse getRecipeDetailsFromApiByRcpSeq(String rcpSeq) {
        log.info("[RecipeService] getRecipeDetailsFromApiByRcpSeq 호출됨 - rcpSeq: {}", rcpSeq);
        RestTemplate restTemplate = new RestTemplate();
        // RCP_SEQ로 조회할 때는 보통 단일 결과이므로 startIndex=1, endIndex=1로 설정
        String apiURL = "http://openapi.foodsafetykorea.go.kr/api/"
                + apiKey
                + "/COOKRCP01"
                + "/json"
                + "/1/1" // 단일 레시피 조회
                + "/RCP_SEQ=" // RCP_SEQ 파라미터 사용
                + rcpSeq;
        // log.info("[RecipeService] API URL (getRecipeDetailsFromApiByRcpSeq): {}", apiURL);
        final HttpEntity<String> entity = new HttpEntity<>(null, new org.springframework.http.HttpHeaders());
        try {
            GetRecipeOpenResponse response = restTemplate.exchange(apiURL, HttpMethod.GET, entity, GetRecipeOpenResponse.class)
                    .getBody();
            // try {
            //     // 기존 로그 유지 (상세 정보 확인용)
            //     // log.info("[RecipeService] getRecipeDetailsFromApiByRcpSeq - 외부 API 응답: {}", objectMapper.writeValueAsString(response));
            // } catch (JsonProcessingException e) {
            //     log.warn("[RecipeService] getRecipeDetailsFromApiByRcpSeq - 외부 API 응답 JSON 변환 실패", e);
            //     // log.info("[RecipeService] getRecipeDetailsFromApiByRcpSeq - 외부 API 응답 (raw): {}", response);
            // }
            return response;
        } catch (Exception e) {
            // API 호출 실패 시 로깅 및 null 또는 예외 반환 처리
            log.error("[RecipeService] Error fetching recipe details from API for RCP_SEQ {}: {}", rcpSeq, e.getMessage(), e);
            // 필요에 따라 CustomException(OPEN_API_SERVER_ERROR) 등을 던질 수 있음
            return null; // 또는 빈 GetRecipeOpenResponse 객체 반환
        }
    }

    public GetRecipeOpenResponse getMenuDataFromApiByRecipeName(String recipeName) {
        // 레시피명으로 공공데이터에서 레시피 정보 조회 (startIndex, endIndex는 임의 지정)
        return getMenuDataFromApi(recipeName, 1, 10);
    }

    public Recipe fetchRecipeData(GetRecipeOpenResponse openApiResponse) {
        // try {
        //     // log.info("[RecipeService] fetchRecipeData 호출됨 - openApiResponse: {}", objectMapper.writeValueAsString(openApiResponse));
        // } catch (JsonProcessingException e) {
        //     log.warn("[RecipeService] fetchRecipeData - openApiResponse JSON 변환 실패", e);
        //     // log.info("[RecipeService] fetchRecipeData - openApiResponse (raw): {}", openApiResponse);
        // }

        // openApiResponse에서 첫 번째 레시피 데이터만 추출하여 Recipe 객체로 변환
        if (openApiResponse == null || openApiResponse.cookRcpInfo() == null || openApiResponse.cookRcpInfo().row() == null || openApiResponse.cookRcpInfo().row().isEmpty()) {
            log.warn("[RecipeService] fetchRecipeData - 유효한 RecipeData 없음. null 반환.");
            return null;
        }
        GetRecipeOpenResponse.RecipeData data = openApiResponse.cookRcpInfo().row().get(0);
        // 추가된 로그: fetchRecipeData에서 사용하는 RecipeData의 영양 정보
        // log.info("[RecipeService] fetchRecipeData - Source RecipeData: RCP_SEQ={}, INFO_ENG={}, INFO_CAR={}, INFO_PRO={}, INFO_FAT={}, INFO_NA={}", data.recipeSeq(), data.infoCal(), data.infoCar(), data.infoPro(), data.infoFat(), data.infoNa());

        Recipe recipe = new Recipe(
                data.recipeName(),
                List.of(data.partsDetails().split("\\\\n|, |,|\\\\\\\\n")), // 수정된 split 조건
                data.getManuals(),
                data.getManualImages(),
                data.infoCal(),
                data.infoNa(),
                data.infoFat(),
                data.infoPro(),
                data.infoCar(),
                data.attFileNoMk(),
                data.attFileNoMain(),
                data.recipeSeq() // rcpSeq 추가
        );
        // try {
        //     // log.info("[RecipeService] fetchRecipeData - 변환된 Recipe 객체: {}", objectMapper.writeValueAsString(recipe));
        // } catch (JsonProcessingException e) {
        //     log.warn("[RecipeService] fetchRecipeData - 변환된 Recipe 객체 JSON 변환 실패", e);
        //     // log.info("[RecipeService] fetchRecipeData - 변환된 Recipe 객체 (raw): {}", recipe);
        // }
        return recipe;
    }
}


