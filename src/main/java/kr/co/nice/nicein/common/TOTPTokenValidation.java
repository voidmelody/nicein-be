package kr.co.nice.nicein.common;

import de.taimos.totp.TOTP;
import org.apache.commons.codec.binary.Base32;
import org.apache.commons.codec.binary.Hex;

public class TOTPTokenValidation {

    public boolean validate(String memberKey, String inputCode){
        String code = getTOTPCode(memberKey);
        return code.equals(inputCode);
    }

    public String getTOTPCode(String memberKey){
        Base32 base32 = new Base32();
        byte[] bytes = base32.decode(memberKey);
        String hexKey = Hex.encodeHexString(bytes);
        return TOTP.getOTP(hexKey);
    }
}
