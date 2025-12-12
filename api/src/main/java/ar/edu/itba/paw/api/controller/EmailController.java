package ar.edu.itba.paw.api.controller;

import ar.edu.itba.paw.api.dto.EmailVerificationRequestDTO;
import ar.edu.itba.paw.api.dto.ForgotPasswordRequestDTO;
import ar.edu.itba.paw.api.dto.ResendVerificationRequestDTO;
import ar.edu.itba.paw.api.dto.ResetPasswordRequestDTO;
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

    @POST
    @Path(ApiUriConstants.EMAIL_VERIFICATION)
    public Response verifyEmail(@Valid EmailVerificationRequestDTO verificationRequest) {
        LOGGER.info("Email verification request received");
        userService.verify(VerificationType.VERIFY_EMAIL, verificationRequest.getCode());
        return Response.noContent().build();
    }

    @POST
    @Path(ApiUriConstants.EMAIL_VERIFICATION + ApiUriConstants.RESEND_VERIFICATION)
    public Response resendVerificationEmail(@Valid ResendVerificationRequestDTO resendRequest) {
        LOGGER.info("Resend verification email request received");

        User user = userService.findByEmail(resendRequest.getEmail());
        userService.createVerification(VerificationType.VERIFY_EMAIL, user);

        return Response.noContent().build();
    }

    @POST
    @Path(ApiUriConstants.PASSWORD_FORGOT)
    public Response forgotPassword(@Valid ForgotPasswordRequestDTO forgotPasswordRequest) {
        LOGGER.info("Forgot password request received");

        User user = userService.findByEmail(forgotPasswordRequest.getEmail());
        userService.createVerification(VerificationType.VERIFY_FORGOT_PASSWORD, user);

        return Response.noContent().build();
    }

    @POST
    @Path(ApiUriConstants.PASSWORD_RESET)
    public Response resetPassword(@Valid ResetPasswordRequestDTO resetPasswordRequest) {
        LOGGER.info("Reset password request received");

        long userId = userService.verify(VerificationType.VERIFY_FORGOT_PASSWORD, resetPasswordRequest.getCode());
        userService.changePassword(userId, resetPasswordRequest.getPassword());

        return Response.noContent().build();
    }
}
