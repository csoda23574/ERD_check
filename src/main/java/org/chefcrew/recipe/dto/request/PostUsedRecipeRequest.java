package org.chefcrew.recipe.dto.request;

import java.util.List;

public record PostUsedRecipeRequest(
        String id,
        String rcpSeq,
        String title,
        List<String> ingredients,
        List<String> steps,
        String thumbnail,
        Float calorie,
        Float carbohydrate,
        Float protein,
        Float fat,
        Float natrium,
        Boolean cooked // "요리 완료" 여부 필드 추가
) {
    // boolean cooked 필드에 대한 명시적 getter가 필요하지 않음 (record에서 자동 생성)
    // 필요하다면 기본값을 제공하는 compact constructor를 정의할 수 있음
    // public PostUsedRecipeRequest {
    //     if (cooked == null) {
    //         cooked = false; // 기본값으로 false 설정
    //     }
    // }
}