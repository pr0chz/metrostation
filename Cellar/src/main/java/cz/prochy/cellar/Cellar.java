/*
 *     MetroStation
 *     Copyright (C) 2015, 2016, 2017 Jiri Pokorny
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package cz.prochy.cellar;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    final static Logger logger = LoggerFactory.getLogger(Cellar.class);
    final Random random = new Random();

    @RequestMapping(value = "/store", method = RequestMethod.POST)
    public ResponseEntity<String> store(@RequestBody String body, HttpServletRequest request) {

        String filename = "blob_" + System.currentTimeMillis() + "_" + random.nextInt(100000);
        try {
            FileUtils.writeStringToFile(new File(filename), body);
            logger.info("Written blob: " + filename);
        } catch (IOException e) {
            logger.error("Failed to write blob: " + filename);
        }
        return respond();
    }

    @RequestMapping(value = "/error")
    public ResponseEntity<String> error(HttpServletRequest request) {
        logger.info("Returning error response to " + request.getRemoteHost());
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
