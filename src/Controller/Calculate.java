package Controller;

import java.math.BigDecimal;
import javax.swing.JButton;
import javax.swing.JTextField;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author HoaPC
 */
public class Calculate {

    private BigDecimal firstNum;
    private BigDecimal secondNum;
    private final JTextField text;
    private boolean reset = false;
    private boolean isMR = false;
    private boolean process = false; // Check process is calculating
    private int operator = -1;
    //
    private BigDecimal memory = new BigDecimal("0"); // BigDecimal dùng để convert String to number

    public Calculate(JTextField text) {
        this.text = text;
        operator = -1;
    }

    public void setOperator(int operator) {
        this.operator = operator;
    }

    public void pressNumber(JButton btn) {
        BigDecimal temp;
        String value = btn.getText();
        //Nếu process hoặc reset là true -> xóa số cũ trên màn hình và set lại số mới
        //Nếu process là false thì thêm dồn số mới nhập sau số cũ lên screen 
        if (process || reset) {
            text.setText("0"); 
            process = false;
            reset = false;
        }
        isMR = false;
        temp = new BigDecimal(text.getText() + value);
        text.setText(temp + "");
    }

    public void pressDot() {
        if (process || reset) {
            // Set text = 0 để gặp trường hợp người dùng nhập '.' thì màn hình sẽ hiện thị '0.'     
            text.setText("0");
            process = false;
            reset = false;
        }
        if (!text.getText().contains(".")) {
            text.setText(text.getText() + ".");
        }
    }

    //return number from resultScreen
    public BigDecimal getValue() {
        if (isMR) {
            return memory;
        }
        String value = text.getText();
        BigDecimal temp = new BigDecimal(value);
        return temp;
    }

    public void pressClear() {
        firstNum = new BigDecimal("0");
        secondNum = new BigDecimal("0");
        operator = -1;
    }

    //thực hiện các phép tính cộng trừ nhân chia
    public void calculate() {
        boolean flag = false;
        /*process là true khi đang thực hiện phép tính hoặc nhấn dấu =.
        khi này số trên màn hình không thay đổi (firstNum)
        ->khi pressNum sau đó pressCalculate -> fristNum & process = true
        */
        if (!process) {//nếu không có dk này -> secondNum = firstNum & thực hiện phép tính
            if (operator == -1) {
                firstNum = getValue();
            } else {
                secondNum = getValue();
                switch (operator) {
                    case 1:
                        firstNum = firstNum.add(secondNum);
                        break;
                    case 2:
                        firstNum = firstNum.subtract(secondNum);
                        break;
                    case 3:
                        firstNum = firstNum.multiply(secondNum);
                        break;
                    case 4:
                        if (secondNum.doubleValue() != 0) {
                            double result = firstNum.doubleValue() / secondNum.doubleValue();
                            firstNum = new BigDecimal(result + "");
                            break;
                        } else {
                            flag = true;
                        }
                }
            }
            text.setText(firstNum + "");
            if (flag) {
                text.setText("ERROR");
            }
            process = true;
            reset = true;
        }
    }

    public void pressResult() {
        if (!text.getText().equals("ERROR")) {
            calculate();
            operator = -1;
        } else {
            text.setText(firstNum + "");
        }
    }

    public void pressNegate() {
        pressResult();
        text.setText(getValue().negate() + "");
        process = false;
        // Neu ko set lại reset thì khi kết quả là số negate, sau đó nhập vào số mới, sẽ bị nối cuối vào số đó
        reset = true;
    }

    public void pressSqrt() {
        pressResult();
        BigDecimal result = getValue();
        if (result.doubleValue() >= 0) {
            String display = Math.sqrt(result.doubleValue()) + "";
            if (display.endsWith(".0")) {
                display = display.replace(".0", "");
            }
            text.setText(display);
            process = false;
        } else {
            text.setText("ERROR");
        }
        reset = true;
    }

    public void pressPercent() {
        pressResult();
        text.setText((getValue().doubleValue()) / 100 + "");
        process = false;
        reset = true;
    }

    public void pressInvert() {

        pressResult();
        double result = getValue().doubleValue();
        if (result != 0) {
            text.setText((1 / result) + "");
            process = false;
        } else {
            text.setText("ERROR");
        }
        reset = true;
    }

    public void pressMR() {
        text.setText(memory + "");
        isMR = true;
    }

    public void pressMC() {
        memory = new BigDecimal("0");
    }

    public void pressMAdd() {
        memory = memory.add(getValue());
        process = false;
        reset = true;
    }

    public void pressMSub() {
        memory = memory.add(getValue().negate());
        process = false;
        reset = true;
    }
}
