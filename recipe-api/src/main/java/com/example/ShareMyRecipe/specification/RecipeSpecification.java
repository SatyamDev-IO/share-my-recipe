package com.example.ShareMyRecipe.specification;

import com.example.ShareMyRecipe.entity.Recipe;
import com.example.ShareMyRecipe.enums.Status;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class RecipeSpecification {

    public static Specification<Recipe> getRecipes(
            String q,
            LocalDateTime from,
            LocalDateTime to,
            Long chefId
    ) {
        return new Specification<Recipe>() {
            @Override
            public Predicate toPredicate(Root<Recipe> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<Predicate> predicates = new ArrayList<>();

                predicates.add(cb.equal(root.get("status"), Status.PUBLISHED));

                if (q != null && !q.isEmpty()) {
                    String likePattern = "%" + q.toLowerCase() + "%";

                    Predicate title = cb.like(cb.lower(root.get("title")), likePattern);
                    Predicate summary = cb.like(cb.lower(root.get("summary")), likePattern);
                    Predicate ingredients = cb.like(cb.lower(root.get("ingredients")), likePattern);
                    Predicate steps = cb.like(cb.lower(root.get("steps")), likePattern);

                    predicates.add(cb.or(title, summary, ingredients, steps));
                }

                if (from != null) {
                    predicates.add(cb.greaterThanOrEqualTo(root.get("publishedAt"), from));
                }

                if (to != null) {
                    predicates.add(cb.lessThanOrEqualTo(root.get("publishedAt"), to));
                }

                if (chefId != null) {
                    predicates.add(cb.equal(root.get("chef").get("id"), chefId));
                }

                return cb.and(predicates.toArray(new Predicate[0]));
            }
        };

    }
}
