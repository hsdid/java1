package org.example;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Main {
    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);


//        try (BufferedWriter out = new BufferedWriter(new FileWriter("books.csv"))) {
//            for (int i = 0; i < 20_000_000; i++) {
//                int randomAuthor = (int) ((Math.random() * 2) + 1);
//                out.write("title_" + i + ",LEKTURA," + randomAuthor);
//                out.newLine();
//            }
//        } catch (Exception exc) {
//            exc.printStackTrace();
//        }
    }

    // BEAN SCOPE
    // SINGLETON, prototype, session, request


}