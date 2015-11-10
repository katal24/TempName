package hello;

import org.springframework.boot.*;
import org.springframework.boot.autoconfigure.*;
import org.springframework.stereotype.*;
import org.springframework.web.bind.annotation.*;
import products.Product;
import java.util.LinkedList;
import java.util.List;

@Controller
@EnableAutoConfiguration
public class SampleController {

    @RequestMapping("/")
    @ResponseBody
    String home() {
        return "Hello World!";
    }


    @RequestMapping("/getIndex/")
    @ResponseBody
    String index() {
        return "witaj na stronie index!";
    }

    @RequestMapping("/getProducts/")
    @ResponseBody
    List<Product> getProducts() {
        List<Product> lista= new LinkedList<Product>();
        Product nutella = new Product();
        Product lizak = new Product();

        nutella.setId(2);
        nutella.setCarbo(200);
        nutella.setFat(43);
        nutella.setName("nutella");
        nutella.setGrade(10);
        nutella.setKcal(200);
        nutella.setWhey(30);


        lista.add(nutella);
        lizak.setId(1);
        lizak.setCarbo(100);
        lizak.setFat(33);
        lizak.setName("lziaczek");
        lizak.setGrade(10);
        lizak.setKcal(200);
        lizak.setWhey(30);
        lista.add(lizak);
        return lista;
    }


    public static void main(String[] args) throws Exception {
        SpringApplication.run(SampleController.class, args);
    }
}