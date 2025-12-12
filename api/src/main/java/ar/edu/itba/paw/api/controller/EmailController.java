package ar.edu.itba.paw.api.controller;

import ar.edu.itba.paw.api.dto.EmailVerificationRequestDTO;
import ar.edu.itba.paw.api.dto.ResendVerificationRequestDTO;
import ar.edu.itba.paw.api.utils.ApiUriConstants;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.models.VerificationType;
import ar.edu.itba.paw.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path(ApiUriConstants.EMAIL_BASE)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class EmailController extends BaseController {

    private static final Logger LOGGER = LoggerFactory.getLogger(EmailController.class);

    @Autowired
    private UserService userService;

    /**
     * Verify user email with verification code
     * POST /api/email/verification
     */
    @POST
    @Path(ApiUriConstants.EMAIL_VERIFICATION)
    public Response verifyEmail(@Valid EmailVerificationRequestDTO verificationRequest) {
        LOGGER.info("Email verification request received");

        Long userId = userService.verify(VerificationType.VERIFY_EMAIL, verificationRequest.getCode());

        if (userId == null) {
            throw new NotFoundException("Invalid or expired verification code");
        }

        return Response.noContent().build();
    }

    /**
     * Resend verification email
     * POST /api/email/verification/resend
     */
    @POST
    @Path(ApiUriConstants.EMAIL_VERIFICATION + ApiUriConstants.RESEND_VERIFICATION)
    public Response resendVerificationEmail(@Valid ResendVerificationRequestDTO resendRequest) {
        LOGGER.info("Resend verification email request received");

        User user = userService.findByEmail(resendRequest.getEmail());
        userService.createVerification(VerificationType.VERIFY_EMAIL, user);

        return Response.noContent().build();
    }
}
