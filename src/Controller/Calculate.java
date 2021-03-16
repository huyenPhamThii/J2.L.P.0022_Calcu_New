/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import java.math.BigDecimal;
import java.math.RoundingMode;
import javax.swing.JButton;
import javax.swing.JTextField;

/**
 *
 * @author HuyenPT
 */
public class Calculate {

    private JTextField txtScreen;
    private BigDecimal firstNum;
    private BigDecimal secondNum;
    private boolean process = false; //check process is calculating
    private boolean backSpace = false;
    private boolean isError = false;
    private boolean isMR = false;
    private int operator = -1;
    private BigDecimal memory = new BigDecimal("0");

    public Calculate(JTextField text) {
        this.txtScreen = text;
        operator = -1;
    }

    public void pressNumber(JButton btn) {
        BigDecimal temp;
        String value = btn.getText();

        if (process || isError) {//nhập số mới
            txtScreen.setText("0");//set về 0 để khi nhập số tiếp theo không còn số cũ ở đầu
            //set lại để bấm được nhiều lần cho 1 btn
            process = false;
            isError = false;
        }
        //nhập tiếp 
        temp = new BigDecimal(txtScreen.getText() + value);
        txtScreen.setText(temp + "");
        //có thể xóa được
        backSpace = true;
        isMR = false;
    }

    public void setOperator(int ope) {
        this.operator = ope;
    }

    //return number from resultScreen
    public BigDecimal getValue() {
        if (isMR) {
            return memory;
        }
        String value = txtScreen.getText();
        BigDecimal temp = new BigDecimal(value);
        return temp;
    }

    //calculating
    public void calculate() {
        boolean flag = false;
        /*process là true khi đang thực hiện phép tính hoặc nhấn dấu =.
        khi này số trên màn hình không thay đổi (firstNum)
        **khi nhấn pressNum sau đó nhấn pressCalculate -> firstNum / process = true
        pressNum sau đó nhấn pressCalculate ->secondNum -> calculate
         */
        //process chỉ = false sau khi nhập số và = true khi nhập phép toán

        if (!process) {//nếu không có dk này khi nhập 2 phép toán liên tiếp 
            //-> secondNum(sau) = firstNum+secondNum (trước) (vì 2num lấy value từ screen) -> thực hiện phép tính
            if (operator == -1) {
                firstNum = getValue();//get value from screen
                System.out.println("fi:" + firstNum);
            } else {
                secondNum = getValue();
                System.out.println("se:" + secondNum);
                switch (operator) {
                    case 1: {
                        firstNum = firstNum.add(secondNum);
                        break;
                    }
                    case 2: {
                        firstNum = firstNum.subtract(secondNum);
                        break;
                    }
                    case 3: {
                        firstNum = firstNum.multiply(secondNum).setScale(7, RoundingMode.HALF_UP);//??? làm tròn 7 chữ số sau dấu ','
                        break;
                    }
                    case 4: {
                        if (secondNum.doubleValue() != 0) {
                            firstNum = firstNum.divide(secondNum, 8, RoundingMode.HALF_UP);
                            break;
                        } else {
                            flag = true;
                        }

                    }
                }
            }
            backSpace = true;
            process = true;
            /*
            stripTrailingZeros(): xóa chữ số không thừa sau dấu phẩy
            toPlainString(): display reality number with string.
            vd: 1x1000000000 = 1e^16 -> 1000000000 
             */
            txtScreen.setText(firstNum.stripTrailingZeros().toPlainString() + "");
            if (flag) {
                txtScreen.setText("ERROR");
                isError = true;
            }
        }
    }

    public void pressResult() {
        if (!isError) {
            calculate();
            operator = -1;//result -> firstNum if not result -> secondNum
        } else {
            txtScreen.setText(firstNum.stripTrailingZeros().toPlainString());
            isError = false;
        }
        process = true;
        backSpace = false;//hiển thị kq không cho phép xóa lùi
    }

    public void pressClear() {
        txtScreen.setText("0");
        process = false;
        operator = -1;
        backSpace = false;
        isError = false;
    }

    public void pressNegative() {
        if (isError) {
            return;
        }
        StringBuilder temp = new StringBuilder(txtScreen.getText());
        if (!txtScreen.getText().equalsIgnoreCase("0")) {
            if (temp.charAt(0) != '-') {
                temp.insert(0, '-');
            } else {
                temp.deleteCharAt(0);
            }
        }
        if (!process) {
            backSpace = true;
        } else {
            backSpace = false;
        }
        txtScreen.setText(temp + "");
        isError = false;
    }

    public void pressInvert() {
        if (isError) {
            return;
        }
        double result = getValue().doubleValue();
        if (result != 0) {
            firstNum = new BigDecimal(String.valueOf(1 / Double.parseDouble(getValue() + "")));
//            System.out.println("-----\nvalue:" + getValue());
//            System.out.println("so chia:" + Double.parseDouble(getValue() + ""));
//            System.out.println("inside : " + String.valueOf(1 / Double.parseDouble(getValue() + "")));
//            System.out.println("fi:"+firstNum);
            txtScreen.setText(firstNum.stripTrailingZeros().toPlainString());
        } else {
            txtScreen.setText("EORROR");
            isError = true;
        }
        process = false;
        backSpace = false;
        isError = false;
    }

    public void pressBackSpace() {
        if (backSpace) {
            StringBuilder temp = new StringBuilder(txtScreen.getText());
            if (temp.length() > 1) {
                if (temp.length() == 3 && temp.charAt(0) == '-' && temp.charAt(2) == '.') {
                    //vd: -5. -> 0
                    txtScreen.setText("0");
                } else if (temp.length() == 2 && temp.charAt(0) == '-') {//vd: -5
                    txtScreen.setText("0");
                } else {//xóa ký tự cuối
                    temp.deleteCharAt(temp.length() - 1);
                    txtScreen.setText(temp.toString());
                }
            } else {
                txtScreen.setText("0");
            }
        }
    }

    public void pressPercent() {
        if (isError) {
            return;
        }
        BigDecimal result = getValue().divide(new BigDecimal("100"), 7, RoundingMode.HALF_EVEN);
        txtScreen.setText(result.stripTrailingZeros().toPlainString());
        isError = false;
        backSpace = false;
    }

    public void pressSqrt() {
        if (isError) {
            return;
        }
        double num = getValue().doubleValue();
        if (num < 0) {
            txtScreen.setText("ERROR");
            isError = true;
        } else {
            BigDecimal result = new BigDecimal(Math.sqrt(num)).setScale(7, RoundingMode.HALF_UP);
            txtScreen.setText(result.stripTrailingZeros().toPlainString());
        }
        backSpace = false;
        isError = false;
    }

    public void pressDot() {
        if (process || isError) {
            //Set text = 0 để gặp trường hợp người dùng nhập '.' thì màn hình sẽ hiện thị '0.'
            txtScreen.setText("0.");
            process = false;//số tiếp theo nhập vào được viết sau số cũ
            isError = false;
        }
        BigDecimal num = getValue();
        if (!txtScreen.getText().contains(".")) {
            txtScreen.setText(getValue() + ".");
        }
    }

    public void pressMC() {
        memory = new BigDecimal("0");
    }

    public void pressMR() {
        txtScreen.setText(memory.stripTrailingZeros().toPlainString());
        isMR = true;
        isError = false;
    }

    public void pressMAdd() {
        if (!isError) {
            memory = memory.add(getValue());
        }
        backSpace = false;
    }

    public void pressMSub() {
        if (!isError) {
            memory = memory.add(getValue().negate());
        }
        backSpace = false;
    }

}
