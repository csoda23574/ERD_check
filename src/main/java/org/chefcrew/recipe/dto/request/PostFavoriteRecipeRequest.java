package org.chefcrew.recipe.dto.request;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PostFavoriteRecipeRequest {
    private String rcpSeq; // externalRecipeId
    private String title;
    private List<String> ingredients;
    private List<String> steps;
    private String thumbnail;
    private String calorie;
    private String carbohydrate;
    private String protein;
    private String fat;
    private String natrium;
}
