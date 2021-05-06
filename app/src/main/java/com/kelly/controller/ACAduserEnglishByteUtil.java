package com.kelly.controller;

public class ACAduserEnglishByteUtil {
    public static String printByteArrayHex(byte[] info, int offset, int length) {
        StringBuffer sb = new StringBuffer();
        int i = offset;
        while (i < offset + length) {
            if (i > offset) {
                sb.append(",");
            }
            String hexStr = Integer.toHexString(info[i] < (byte) 0 ? info[i] + 256 : info[i]).toUpperCase();
            if (hexStr.length() == 1) {
                sb.append("0" + hexStr);
            } else {
                sb.append(hexStr);
            }
            i++;
        }
        return sb.toString();
    }

    public static int[] getIntArray(String info) {
        if (info == null || info.equals("ERROR")) {
            return new int[]{0};
        }
        String[] ByteArrayStr = info.split(",");
        int[] ByteArray = new int[ByteArrayStr.length];
        for (int i = 0; i < ByteArrayStr.length; i++) {
            ByteArray[i] = Integer.valueOf(ByteArrayStr[i].substring(0), 16).intValue();
        }
        return ByteArray;
    }

    public static String printIntArrayUnsignInt(int[] info, int offset, int length) {
        StringBuffer sb = new StringBuffer();
        int i = offset;
        while (i < offset + length) {
            if (i > offset) {
                sb.append(",");
            }
            sb.append(info[i] < 0 ? info[i] + 256 : info[i]);
            i++;
        }
        return sb.toString();
    }

    public static String InttoBinaryString(int error) {
        if (error <= 0 || error >= 65536) {
            return "ERROR";
        }
        StringBuffer sb = new StringBuffer();
        String BinaryStr = Integer.toBinaryString(error);
        int j = 0;
        for (int k = 0; k < BinaryStr.length(); k++) {
            if (Integer.parseInt(BinaryStr.substring((BinaryStr.length() - 1) - k, BinaryStr.length() - k)) == 1) {
                if (j > 0) {
                    sb.append(",");
                }
                j++;
                sb.append(ACAduserEnglishSetting.Error_Value_Array[k][1]);
            }
        }
        return sb.toString();
    }

    public static String printIntArrayString(int[] info, int offset, int pro, int pos, String sw) {
        StringBuffer sb = new StringBuffer();
        int length = 0;
        if (pro == 0 || pro == 1) {
            length = 1;
        } else if (pro == 2) {
            length = pos + 1;
        }
        int finaltempInt = 0;
        for (int i = offset; i < offset + length; i++) {
            int tempInt = info[i];
            if (sw == null || !sw.equals("uo")) {
                if (sw != null && sw.equals("h")) {
                    String hexStr = Integer.toHexString(tempInt);
                    if (hexStr.length() == 1) {
                        sb.append("0" + hexStr);
                    } else {
                        sb.append(hexStr);
                    }
                } else if (sw != null && sw.equals("a")) {
                    sb.append((char) tempInt);
                } else if (sw != null && sw.equals("so")) {
                    sb.append((byte) tempInt);
                }
            } else if (pro == 0) {
                sb.append((tempInt / ((int) Math.pow(2.0d, (double) pos))) & 1);
            } else if (pro == 1) {
                sb.append(tempInt);
            } else if (pro == 2) {
                finaltempInt = (finaltempInt * 256) + tempInt;
                if (i == offset + pos) {
                    sb.append(finaltempInt);
                }
            }
        }
        return sb.toString();
    }

    public static int printStringArrayInt(int[] info, int offset, int pro, int pos, String sw, String result) {
        int i;
        if (sw == null || !sw.equals("uo")) {
            if (sw == null || !sw.equals("h")) {
                if (sw == null || !sw.equals("a")) {
                    if (sw != null && sw.equals("so")) {
                        info[offset] = Integer.parseInt(result);
                        return 1;
                    }
                } else if (result.length() != pos + 1) {
                    return 0;
                } else {
                    for (i = offset; i < (offset + pos) + 1; i++) {
                        info[i] = result.charAt(i - offset);
                    }
                    return 1;
                }
            } else if (result.length() != (pos + 1) * 2) {
                return 0;
            } else {
                for (i = offset; i < (offset + pos) + 1; i++) {
                    info[i] = Integer.valueOf(result.substring((i - offset) * 2, ((i - offset) * 2) + 2), 16).intValue();
                }
                return 1;
            }
        } else if (pro == 0) {
            if (result.equals("1")) {
                info[offset] = info[offset] | ((int) Math.pow(2.0d, (double) pos));
                return 1;
            } else if (!result.equals("0")) {
                return 0;
            } else {
                info[offset] = info[offset] & (((int) Math.pow(2.0d, (double) pos)) ^ -1);
                return 1;
            }
        } else if (pro == 1) {
            info[offset] = Integer.parseInt(result);
            return 1;
        } else if (pro == 2) {
            int finaltempInt = Integer.parseInt(result);
            for (i = offset; i < (offset + pos) + 1; i++) {
                info[i] = finaltempInt / ((int) Math.pow(256.0d, (double) (pos - (i - offset))));
                if (i < offset + pos) {
                    finaltempInt -= info[i] * ((int) Math.pow(256.0d, (double) (pos - (i - offset))));
                }
            }
            return 1;
        }
        return 0;
    }

    public static int get_Index(int textview_count, int j, int mode) {
        int colIndex = 0;
        int rowIndex = 0;
        int line_count = (textview_count + -1) % 3 == 0 ? (textview_count - 1) / 3 : ((textview_count - 1) / 3) + 1;
        if ((textview_count - 1) % 3 == 0) {
            if (1 < j && j <= line_count + 1) {
                colIndex = 0;
                rowIndex = j - 1;
            } else if (line_count + 1 < j && j <= (line_count * 2) + 1) {
                colIndex = 1;
                rowIndex = (j - 1) - line_count;
            } else if (line_count + 1 < j && j <= textview_count) {
                colIndex = 2;
                rowIndex = (j - 1) - (line_count * 2);
            }
        } else if ((textview_count - 1) % 3 == 1) {
            if (1 < j && j <= line_count + 1) {
                colIndex = 0;
                rowIndex = j - 1;
            } else if (line_count + 1 < j && j <= line_count * 2) {
                colIndex = 1;
                rowIndex = (j - 1) - line_count;
            } else if (line_count * 2 < j && j <= textview_count) {
                colIndex = 2;
                rowIndex = j - (line_count * 2);
            }
        } else if ((textview_count - 1) % 3 == 2) {
            if (1 < j && j <= line_count + 1) {
                colIndex = 0;
                rowIndex = j - 1;
            } else if (line_count + 1 < j && j <= (line_count * 2) + 1) {
                colIndex = 1;
                rowIndex = (j - 1) - line_count;
            } else if ((line_count * 2) + 1 < j && j <= textview_count) {
                colIndex = 2;
                rowIndex = (j - (line_count * 2)) - 1;
            }
        }
        if (mode == 0) {
            return colIndex;
        }
        return rowIndex;
    }

    public static int get_Index2(int textview_count, int j, int mode) {
        int colIndex = 0;
        int rowIndex = 0;
        int line_count = textview_count % 3 == 0 ? textview_count / 3 : (textview_count / 3) + 1;
        if (textview_count % 3 == 0) {
            if (j > 0 && j <= line_count) {
                colIndex = 0;
                rowIndex = j - 1;
            } else if (line_count < j && j <= line_count * 2) {
                colIndex = 1;
                rowIndex = (j - 1) - line_count;
            } else if (line_count < j && j <= textview_count) {
                colIndex = 2;
                rowIndex = (j - 1) - (line_count * 2);
            }
        } else if (textview_count % 3 == 1) {
            if (j > 0 && j <= line_count) {
                colIndex = 0;
                rowIndex = j - 1;
            } else if (line_count < j && j <= (line_count * 2) - 1) {
                colIndex = 1;
                rowIndex = (j - 1) - line_count;
            } else if ((line_count * 2) - 1 < j && j <= textview_count) {
                colIndex = 2;
                rowIndex = j - (line_count * 2);
            }
        } else if (textview_count % 3 == 2) {
            if (j > 0 && j <= line_count) {
                colIndex = 0;
                rowIndex = j - 1;
            } else if (line_count < j && j <= line_count * 2) {
                colIndex = 1;
                rowIndex = (j - 1) - line_count;
            } else if (line_count * 2 < j && j <= textview_count) {
                colIndex = 2;
                rowIndex = (j - (line_count * 2)) - 1;
            }
        }
        if (mode == 0) {
            return colIndex;
        }
        return rowIndex;
    }

    public static int getVoltRange(String Voltstr, int mode) {
        int max = 0;
        int min = 0;
        switch (Integer.parseInt(Voltstr)) {
            case 12:
                max = 136;
                min = 18;
                break;
            case 14:
                max = 180;
                min = 18;
                break;
            case 24:
                max = 30;
                min = 8;
                break;
            case 36:
                max = 45;
                min = 18;
                break;
            case 48:
                max = 60;
                min = 18;
                break;
            case 60:
                max = 80;
                min = 18;
                break;
            case 72:
                max = 90;
                min = 18;
                break;
            case 84:
                max = 105;
                min = 18;
                break;
            case 96:
                max = 120;
                min = 18;
                break;
        }
        if (mode == 0) {
            return min;
        }
        return max;
    }

    public static int GetRangeLimit(int value) {
        if (value == 140 || value == 142 || value == 144 || value == 174 || value == 176 || value == 228 || value == 256 || value == 300 || value == 304 || value == 308 || value == 310 || value == 312 || value == 314 || value == 316) {
            return 1;
        }
        return 0;
    }
}
