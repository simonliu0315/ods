package gov.sls.ods.util;

import java.nio.charset.Charset;

import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;

/**
 * 參考ein_txt2csv.jar中的 ReplaceProcessor
 * com.cht.dna.txt.util.datamask.processor.HashString
 * 
 */
public class BanMask {
    private HashFunction hf = Hashing.murmur3_128(175938624);
    private static BanMask mask ;

    private BanMask() {
    }

    public static BanMask getInstance() {
        if (null==mask){
            mask = new BanMask();
        }
        return mask;
    }
    
    /**進行mask
     * @param token 欲mask之token
     * @return mask後結果
     */
    public String process(String token) {
        return this.hf.newHasher().putString(token, Charset.forName("UTF8"))
                .hash().toString();
    }

    /**
     * @return 該processor敘述
     */
    public String getDesc() {
        return "輸入Mask字元，回傳Hashed String，\nbenchmark=3\n";
    }
    
    
    public static void main(String[] args){
        System.out.println("27950876 to " + BanMask.getInstance().process("27950876"));
    }
}
