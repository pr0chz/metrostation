package cz.prochy.cellar;

import org.apache.commons.io.FileUtils;
import org.springframework.boot.autoconfigure.web.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.util.Random;

@RestController
public class Cellar implements ErrorController {

    final Random random = new Random();

    @RequestMapping(value = "/store", method = RequestMethod.POST)
    public ResponseEntity<String> store(@RequestBody String body, HttpServletRequest request) {

        String filename = "blob_" + System.currentTimeMillis() + "_" + request.getRemoteAddr() + "_" + random.nextInt(1000);
        try {
            FileUtils.writeStringToFile(new File(filename), body);
            System.out.println("Written blob: " + filename);
        } catch (IOException e) {
            System.out.println("Failed to write blob: " + filename);
        }
        return respond();
    }

    @RequestMapping(value = "/error")
    public ResponseEntity<String> error() {
        return respond();
    }

    @Override
    public String getErrorPath() {
        return "/error";
    }

    private ResponseEntity<String> respond() {
        return new ResponseEntity<>("No.", HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
