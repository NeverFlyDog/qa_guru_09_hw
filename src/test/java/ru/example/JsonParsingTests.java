package ru.example;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.example.model.Product;
import ru.example.model.ProductsResponse;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;

public class JsonParsingTests {
    private static final String filename = "products.json";
    private final ClassLoader classLoader = getClass().getClassLoader();

    @DisplayName("Проверка содержимого json-файла")
    @Test
    void jsonParsingTest() throws IOException {
        try (Reader reader = new InputStreamReader(classLoader.getResourceAsStream(filename))) {
            ObjectMapper mapper = new ObjectMapper();

            ProductsResponse response = mapper.readValue(reader, ProductsResponse.class);

            Assertions.assertEquals(194, response.getTotal());
            Assertions.assertEquals(5, response.getProducts().size());

            Product product1 = response.getProducts().getFirst();
            Assertions.assertEquals(7826546, product1.getId());
            Assertions.assertEquals("Essence Mascara Lash Princess", product1.getTitle());
            Assertions.assertEquals(9.99, product1.getPrice());
            Assertions.assertEquals(List.of("beauty", "mascara"), product1.getTags());
        }
    }
}
