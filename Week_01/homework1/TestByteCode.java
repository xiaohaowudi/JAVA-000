package Week1.homework;

public class TestByteCode {
    public static void main(String[] args) {
        int a = 100;
        int b = 33;

        int add_val = a + b;
        int sub_val = a - b;
        int mul_val = a * b;
        int div_val = a / b;

        if (a <= 100) {
            a = 10;
        }

        for (int i = 0; i < a; i++) {
            b += 1;
        }
    }
}


/*
字节码

LocalVariableTable:
        Start  Length  Slot  Name   Signature
           37      15     7     i   I
            0      53     0  args   [Ljava/lang/String;
            3      50     1     a   I
            6      47     2     b   I
           10      43     3 add_val   I
           15      38     4 sub_val   I
           20      33     5 mul_val   I
           25      28     6 div_val   I

public static void main(java.lang.String[]);
    descriptor: ([Ljava/lang/String;)V
    flags: (0x0009) ACC_PUBLIC, ACC_STATIC
    Code:
      stack=2, locals=8, args_size=1
         0: bipush        100
         2: istore_1
         3: bipush        33
         5: istore_2
         6: iload_1
         7: iload_2
         8: iadd
         9: istore_3
        10: iload_1
        11: iload_2
        12: isub
        13: istore        4
        15: iload_1
        16: iload_2
        17: imul
        18: istore        5
        20: iload_1
        21: iload_2
        22: idiv
        23: istore        6
        25: iload_1
        26: bipush        100
        28: if_icmpgt     34
        31: bipush        10
        33: istore_1
        34: iconst_0
        35: istore        7
        37: iload         7
        39: iload_1
        40: if_icmpge     52
        43: iinc          2, 1
        46: iinc          7, 1
        49: goto          37
        52: return


 */