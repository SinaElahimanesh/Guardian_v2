package ir.guardianapp.guardian_v2.DrivingStatus.time;

public class Month {

    public static String getPersianMonth(int month) {
        String monthStr;
        switch (month) {
            case 1:
                monthStr = "فروردین ماه";
                break;
            case 2:
                monthStr = "اردیبهشت ماه";
                break;
            case 3:
                monthStr = "خرداد ماه";
                break;
            case 4:
                monthStr = "تیر ماه";
                break;
            case 5:
                monthStr = "مرداد ماه";
                break;
            case 6:
                monthStr = "شهریور ماه";
                break;
            case 7:
                monthStr = "مهر ماه";
                break;
            case 8:
                monthStr = "آبان ماه";
                break;
            case 9:
                monthStr = "آذر ماه";
                break;
            case 10:
                monthStr = "دی ماه";
                break;
            case 11:
                monthStr = "بهمن ماه";
                break;
            case 12:
                monthStr = "اسفند ماه";
                break;
            default:
                monthStr = "فروردین ماه";
                break;
        }
        return monthStr;
    }
}
