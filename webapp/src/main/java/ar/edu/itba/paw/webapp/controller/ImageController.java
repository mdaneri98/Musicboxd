package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.services.ImageService;
import ar.edu.itba.paw.webapp.exception.ImageNotFoundException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class ImageController {

    private final ImageService imageService;

    public ImageController(ImageService imageService) {
        this.imageService = imageService;
    }

    @RequestMapping(value = "/images/{id:\\d+}", method = RequestMethod.GET)
    public ResponseEntity<byte[]> index(@PathVariable long id) {
        byte[] array = imageService.findById(id).orElseThrow(ImageNotFoundException::new).getBytes();

        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-type", MediaType.IMAGE_JPEG_VALUE);
        headers.set("Content-Disposition", String.format("inline; filename=\"image_%d.jpg\"", id));

        return ResponseEntity.status(HttpStatus.OK).headers(headers).body(array);
    }


}
