package verifier;

public class Util {
    public static void main(String[] args) {
    }
    static int[] POWER2;

    public static int[] getPowers2(){
        if (POWER2 == null){
            POWER2 = new int[31];
            for(int i =0;i<31;i++){
                POWER2[i] = 1<<i;
                System.out.println(i + " " + POWER2[i]);
            }
        }
        return POWER2;
    }

    /**
     * return the binary representation of num
     * e.g. num = 10, bits = 4, return an array of {0,1,0,1}
     */
    public static int[] CalBinRep(long num, int bits)
    {
        if(bits == 0) return new int[0];

        int [] binrep = new int[bits];
        long numtemp = num;
        for(int i = bits; i >0; i--)
        {
            long abit = numtemp & getPowers2()[i - 1];
            if(abit == 0)
            {
                binrep[i - 1] = 0;
            }else
            {
                binrep[i - 1] = 1;
            }
            numtemp = numtemp - abit;
        }
        return binrep;
    }
}
