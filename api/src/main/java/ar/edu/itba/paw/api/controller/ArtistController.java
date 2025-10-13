package ar.edu.itba.paw.api.controller;

import ar.edu.itba.paw.api.mapper.ArtistResourceMapper;
import ar.edu.itba.paw.api.mapper.CollectionResourceMapper;
import ar.edu.itba.paw.api.mapper.ReviewResourceMapper;
import ar.edu.itba.paw.api.models.ArtistResource;
import ar.edu.itba.paw.api.models.CollectionResource;
import ar.edu.itba.paw.api.models.ReviewResource;
import ar.edu.itba.paw.api.utils.ApiUriConstants;
import ar.edu.itba.paw.models.dtos.ArtistDTO;
import ar.edu.itba.paw.models.FilterType;
import ar.edu.itba.paw.models.dtos.ReviewDTO;
import ar.edu.itba.paw.services.ArtistService;
import ar.edu.itba.paw.services.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path(ApiUriConstants.ARTISTS_BASE)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ArtistController extends BaseController {

    @Autowired
    private ArtistService artistService;

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private ArtistResourceMapper artistResourceMapper;

    @Autowired
    private ReviewResourceMapper reviewResourceMapper;

    @Autowired
    private CollectionResourceMapper collectionResourceMapper;

    @GET
    public Response getAllArtists(
            @QueryParam("search") String search,
            @QueryParam("page") @DefaultValue("1") int page,
            @QueryParam("size") @DefaultValue("20") int size,
            @QueryParam("filter") @DefaultValue("FIRST") FilterType filter) {
        
        List<ArtistDTO> artistDTOs = artistService.findPaginated(FilterType.FIRST, page, size);
        List<ArtistResource> artistResources = artistResourceMapper.toResourceList(artistDTOs, getBaseUrl());
        Long totalCount = artistService.countAll();
        
        CollectionResource<ArtistResource> collection = collectionResourceMapper.createCollection(
                artistResources, totalCount, page, size, getBaseUrl(), ApiUriConstants.ARTISTS_BASE);
        
        return buildResponse(collection);
    }

    @GET
    @Path(ApiUriConstants.ID)
    public Response getArtist(@PathParam("id") Long id) {
        ArtistDTO artistDTO = artistService.findById(id);
        ArtistResource artistResource = artistResourceMapper.toResource(artistDTO, getBaseUrl());
        return buildResponse(artistResource);
    }

    @POST
    public Response createArtist(@Valid ArtistDTO artistDTO) {
        ArtistDTO responseDTO = artistService.create(artistDTO);
        ArtistResource artistResource = artistResourceMapper.toResource(responseDTO, getBaseUrl());
        return buildCreatedResponse(artistResource);
    }

    @PUT
    @Path(ApiUriConstants.ID)
    public Response updateArtist(@PathParam("id") Long id, @Valid ArtistDTO artistDTO) {
        artistDTO.setId(id);
        ArtistDTO responseDTO = artistService.update(artistDTO);
        ArtistResource artistResource = artistResourceMapper.toResource(responseDTO, getBaseUrl());
        return buildResponse(artistResource);
    }

    @DELETE
    @Path(ApiUriConstants.ID)
    public Response deleteArtist(@PathParam("id") Long id) {
        artistService.delete(id);
        return buildNoContentResponse();
    }

    @GET
    @Path(ApiUriConstants.ARTIST_REVIEWS)
    public Response getArtistReviews(
            @PathParam("id") Long id,
            @QueryParam("page") @DefaultValue("1") int page,
            @QueryParam("size") @DefaultValue("20") int size,
            @QueryParam("loggedUserId") Long loggedUserId) {
        // TODO: Obtener loggedUserId del contexto de seguridad
        
        List<ReviewDTO> reviews = reviewService.findArtistReviewsPaginated(id, page, size, loggedUserId);
        List<ReviewResource> reviewResources = reviewResourceMapper.toResourceList(reviews, getBaseUrl());
        Long totalCount = reviewService.countAll();
        
        CollectionResource<ReviewResource> collection = collectionResourceMapper.createCollection(
                reviewResources, totalCount, page, size, getBaseUrl(), ApiUriConstants.ARTIST_REVIEWS);
        
        return buildResponse(collection);
    }
}

