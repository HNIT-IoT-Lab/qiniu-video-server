package cn.hnit.utils;

public class IdCradUtil {

    /**
     * 通过身份证号获取性别
     * @param IdNo String
     * @return Integer 0-女 1-男
     */
    public static Integer getSexFromId(String IdNo)
    {
        String tIdNo = IdNo.trim();
        if (tIdNo.length() != 15 && tIdNo.length() != 18)
        {
            return null;
        }
        String sex = "";
        if (tIdNo.length() == 15)
        {
            sex = tIdNo.substring(14, 15);
        }
        else
        {
            sex = tIdNo.substring(16, 17);
        }
        try
        {
            int iSex = Integer.parseInt(sex);
//            iSex = iSex % 2;
            iSex %= 2;
            if (iSex == 0)
            {
                return 0;
            }
            if (iSex == 1)
            {
                return 1;
            }
        }
        catch (Exception ex)
        {
            return null;
        }
        return null;
    }
}
