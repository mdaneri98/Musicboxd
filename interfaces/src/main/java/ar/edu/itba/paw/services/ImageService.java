package ar.edu.itba.paw.services;

import ar.edu.itba.paw.Image;
import ar.edu.itba.paw.persistence.ImageDao;

import java.util.Optional;

public interface ImageService {

    Optional<Image> findById(Long id);

}