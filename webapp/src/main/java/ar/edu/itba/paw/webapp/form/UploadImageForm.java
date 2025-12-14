package ar.edu.itba.paw.webapp.form;

import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataParam;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class UploadImageForm {

    @FormDataParam("image")
    private FormDataBodyPart formDataBodyPart;

    @NotNull(message = "{validation.image.notnull}")
    @Size(max = 10485760, message = "{validation.image.size}")
    @FormDataParam("image")
    private byte[] bytes;

    public UploadImageForm() {
    }

    public FormDataBodyPart getFormDataBodyPart() {
        return formDataBodyPart;
    }

    public void setFormDataBodyPart(FormDataBodyPart formDataBodyPart) {
        this.formDataBodyPart = formDataBodyPart;
    }

    public byte[] getBytes() {
        return bytes;
    }

    public void setBytes(byte[] bytes) {
        this.bytes = bytes;
    }
}

