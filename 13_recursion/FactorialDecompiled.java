class FactorialDecompiled {
    public int fac(int n, int acc) {
        start:
        switch n {
            case 0:
                return acc;
            default:
                acc = n * acc;
                n = n - 1;
                goto start;
        }
    }

    /*
       0: iload_1
       1: istore        4
       3: iload         4
       5: tableswitch   { // 0 to 0
                     0: 24
               default: 28
          }
      24: iload_2
      25: goto          39
      28: iload_1
      29: iconst_1
      30: isub
      31: iload_1
      32: iload_2
      33: imul
      34: istore_2
      35: istore_1
      36: goto          0
      39: ireturn
    */
}
