package org.example.gitinterview;

import java.util.*;

public class Main {
    public static void main(String[] args) {

        List<Integer> numbers = new ArrayList<>(List.of(10, 55, -10, 23));
        System.out.println(sumElements(numbers));

        System.out.println(returnListsWithSumBiggerThan(Arrays.asList(numbers, List.of(10, 50)), 70));
    }

    public static List<List<Integer>> returnListsWithSumBiggerThan(List<List<Integer>> lists, int givenSum) {
        return Optional.ofNullable(lists)
                .orElseGet(Collections::emptyList)
                .stream()
                .filter(Objects::nonNull)
                .filter(list -> sumElements(list) > givenSum)
                .toList();
    }

    public static int sumElements(List<Integer> numbers) {
        return numbers.stream()
                .mapToInt(Integer::intValue)
                .sum();
    }


}
