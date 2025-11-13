package ar.edu.itba.paw.models.dtos;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class ImageDTO {

    @NotNull(message = "{validation.image.base64.notnull}")
    @Size(max = 14000000, message = "{validation.image.base64.size}")
    private String base64;
    
    @NotNull(message = "{validation.image.contentType.notnull}")
    private String contentType;

    private String fileName;

    public String getBase64() {
        return base64;
    }

    public String getContentType() {
        return contentType;
    }

    public String getFileName() {
        return fileName;
    }

    public void setBase64(String base64) {
        this.base64 = base64;
    }
    
    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
