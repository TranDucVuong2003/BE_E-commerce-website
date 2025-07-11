package com.tranvuong.be_e_commerce.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

@Configuration
public class CloudinaryConfig {
    @Bean 
    public Cloudinary cloudinary() {
        return new Cloudinary(ObjectUtils.asMap(
            "cloud_name", "dyyl5ovel",
            "api_key", "294698883867544",
            "api_secret", "_PuCS0MGVImYFAbd_IRy6gDkpIE" 
        ));
    }
}
