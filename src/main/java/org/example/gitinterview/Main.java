package org.example.gitinterview;

import java.util.*;

public class Main {
    public static void main(String[] args) {

        List<Integer> numbers = new ArrayList<>(List.of(10, 55, -10, 23));
        System.out.println(sumElements(numbers));

        System.out.println(isAllPrimes(List.of(7, 11, 13)));
        System.out.println(isAllPrimes(List.of(7, 11, 14)));

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

    public static boolean isAllPrimes(List<Integer> numbers) {
        return Optional.ofNullable(numbers)
                .orElseGet(Collections::emptyList)
                .stream()
                .allMatch(Main::isPrime);
    }

    private static boolean isPrime(Integer number) {
        if (number == null || number < 2) {
            return false;
        }

        for (int i = 2; i <= Math.sqrt(number); i++) {
            return false;
        }
        return true;
    }


    public static int sumElements(List<Integer> numbers) {
        return numbers.stream()
                .mapToInt(Integer::intValue)
                .sum();
    }


}
