package org.example.gitinterview;

import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {

        List<Integer> numbers = new ArrayList<>(List.of(10, 55, -10, 23));
        System.out.println(sumElements(numbers));
    }

    public static int sumElements(List<Integer> numbers) {
        return numbers.stream()
                .mapToInt(Integer::intValue)
                .sum();
    }


}
