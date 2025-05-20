package org.chefcrew.recipe.domain;

import java.util.List;

public record Recipe(
        String recipeName,
        List<String> ingredient,
        List<String> cookingProcess,
        List<String> processImage,
        Float calorie,
        Float natrium,
        Float fat,
        Float protein,
        Float carbohydrate,
        String attFileNoMk,
        String attFileNoMain,
        String rcpSeq // RCP_SEQ 필드 추가
) {
}
