package com.task2.portal.webhook;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
@Component 
public class HmacSigner {
    private final String secret;

    public HmacSigner(@Value ("${app.webhook.secret}") String secret) {
        this.secret = secret;
    }

    public String sign(String payload) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            byte[] digest = mac.doFinal(payload.getBytes(StandardCharsets.UTF_8));
            StringBuilder hex = new StringBuilder(digest.length * 2);
            for (byte b : digest) {
                hex.append(String.format("%02x", b & 0xff));
            }
            return "sha256=" + hex;
        } catch (Exception ex) {
            throw new IllegalStateException("Unable to calculate webhook signature", ex);
        }
    }

    public boolean verify(String payload, String signature) {
        if (signature == null || !signature.startsWith("sha256=")) return false;
        String expected = sign(payload);
        return MessageDigest.isEqual(expected.getBytes(StandardCharsets.UTF_8), signature.getBytes(StandardCharsets.UTF_8));
    }
}
