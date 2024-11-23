package controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

enum Operator {
    PLUS("+"), MINUS("-"), MULTIPLY("*");

    private String symbol;

    Operator(String symbol) {
        this.symbol = symbol;
    }

    public String getSymbol() {
        return symbol;
    }
}
// mỗi thành phần khi tạo biểu thức: number or operator

class Choice {

    boolean isOperator;
    int number;
    Operator operator;

    public Choice() {
    }

    // Constructor cho số
    public Choice(int number) {
        this.isOperator = false;
        this.number = number;
    }

    // Constructor cho phép toán
    public Choice(Operator operator) {
        this.isOperator = true;
        this.operator = operator;
    }

    public boolean isIsOperator() {
        return isOperator;
    }

    public void setIsOperator(boolean isOperator) {
        this.isOperator = isOperator;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public Operator getOperator() {
        return operator;
    }

    public void setOperator(Operator operator) {
        this.operator = operator;
    }

    @Override
    public String toString() {
        return isOperator ? operator.getSymbol() : String.valueOf(number);
    }
}

public class QuestionGenerator {

    private int target;
    private int round;
    private List<String> choices;

    public QuestionGenerator() {
    }

    public QuestionGenerator(int target, int round) {
        this.target = target;
        this.round = round;
        this.choices = generatedChoices();
    }

    public int getRound() {
        return round;
    }

    public void setRound(int round) {
        this.round = round;
    }

    public List<String> getChoices() {
        return choices;
    }

    public void setChoices(List<String> choices) {
        this.choices = choices;
    }

    public int getTarget() {
        return target;
    }

    public void setTarget(int target) {
        this.target = target;
    }

    public String printChoices() {
        String res = "";
        for (String x : choices) {
            res += x + " ";
        }
        return res;
    }

    // taọ bộ từ giá trị mục tiêu cho trước
    public List<String> generatedChoices() {
        List<String> res = new ArrayList<>();
        Random rand = new Random();
        List<Choice> choices = new ArrayList<>();

        int numberRange = round == 1 ? 10 : 30;

        Operator[] operators = round == 1 ? new Operator[]{Operator.PLUS, Operator.MINUS} : new Operator[]{Operator.PLUS, Operator.MINUS, Operator.MULTIPLY};

        // tạo các số ngẫu nhiên
        for (int i = 0; i < (round == 1 ? 7 : 6); i++) { // 7 số ngẫu nhiên vòng 1, 5 số vòng 2
            int randomNum = rand.nextInt(numberRange) + 1;
            choices.add(new Choice(randomNum));
        }
        // Tạo phép toán ngẫu nhiên
        for (int i = 0; i < (round == 1 ? 3 : 4); i++) { // 3 phép toán vòng 1, 5 vòng 2
            Operator randomOp = operators[rand.nextInt(operators.length)];
            choices.add(new Choice(randomOp));
        }
        // đảm bảo có biểu thức đúng
        choices = ensureValidExpression(choices, target, operators, numberRange, round);
        // Xáo trộn các lựa chọn để không đoán được dễ dàng
        Collections.shuffle(choices);
        for (Choice choice : choices) {
            res.add(choice.toString());
        }
        return res;

    }

    // Đảm bảo tạo ra một biểu thức có thể ra kết quả mục tiêu
//    private static List<Choice> ensureValidExpression(List<Choice> choices, int targetResult, Operator[] operators,
//            int numberRange, int round) {
//        Random rand = new Random();
//        int num1, num2, num3;
//        Operator operator1, operator2;
//
//        
//        if (round == 1) {
//            // Vòng 1: 2 toán hạng và 1 phép toán
//            num1 = rand.nextInt(numberRange) + 1;
//            num2 = rand.nextInt(numberRange) + 1;
//            operator1 = operators[rand.nextInt(operators.length)];
//
//            int validResult = calculate(num1, num2, operator1);
//
//            // Đảm bảo kết quả đúng với mục tiêu
//            while (validResult != targetResult) {
//                num1 = rand.nextInt(numberRange) + 1;
//                num2 = rand.nextInt(numberRange) + 1;
//                operator1 = operators[rand.nextInt(operators.length)];
//                validResult = calculate(num1, num2, operator1);
//            }
//
//            // Thêm toán hạng và phép toán vào danh sách lựa chọn
//            choices.set(0, new Choice(num1));
//            choices.set(1, new Choice(operator1));
//            choices.set(2, new Choice(num2));
//
//        } else {
//            // Vòng 2: 3 toán hạng và 2 phép toán
//            num1 = rand.nextInt(numberRange) + 1;
//            num2 = rand.nextInt(numberRange) + 1;
//            num3 = rand.nextInt(numberRange) + 1;
//            operator1 = operators[rand.nextInt(operators.length)];
//            operator2 = operators[rand.nextInt(operators.length)];
//
//            
//            int validResult1 = calculate(num1, num2, operator1);
//            int validResult = calculate(validResult1, num3, operator2); // kết hợp với toán hạng thứ 3
//
//            // Đảm bảo kết quả đúng với mục tiêu
//            while (validResult != targetResult) {
//                num1 = rand.nextInt(numberRange) + 1;
//                num2 = rand.nextInt(numberRange) + 1;
//                num3 = rand.nextInt(numberRange) + 1;
//                operator1 = operators[rand.nextInt(operators.length)];
//                operator2 = operators[rand.nextInt(operators.length)];
//
//                validResult1 = calculate(num1, num2, operator1);
//                validResult = calculate(validResult1, num3, operator2);
//            }
//
//            // Thêm toán hạng và phép toán vào danh sách lựa chọn
//            choices.set(0, new Choice(num1));
//            choices.set(1, new Choice(operator1));
//            choices.set(2, new Choice(num2));
//            choices.set(3, new Choice(operator2));
//            choices.set(4, new Choice(num3));
//        }
//
//        return choices;
//    }
//    private static List<Choice> ensureValidExpression(List<Choice> choices, int targetResult, Operator[] operators,
//            int numberRange, int round) {
//        Random rand = new Random();
//        int num1, num2, num3;
//        Operator operator1, operator2;
//
//        if (round == 1) {
//            // Vòng 1: 2 toán hạng và 1 phép toán
//            int validResult;
//            do {
//                // Sinh số ngẫu nhiên đảm bảo không bằng targetResult
//                do {
//                    num1 = rand.nextInt(numberRange) + 1;
//                } while (num1 == targetResult);
//
//                do {
//                    num2 = rand.nextInt(numberRange) + 1;
//                } while (num2 == targetResult);
//
//                operator1 = operators[rand.nextInt(operators.length)];
//                validResult = calculate(num1, num2, operator1);
//            } while (validResult != targetResult);
//
//            // Thêm toán hạng và phép toán vào danh sách lựa chọn
//            choices.set(0, new Choice(num1));
//            choices.set(1, new Choice(operator1));
//            choices.set(2, new Choice(num2));
//        } else {
//            // Vòng 2: 3 toán hạng và 2 phép toán
//            int validResult, validResult1;
//            do {
//                // Sinh số ngẫu nhiên đảm bảo không bằng targetResult
//                do {
//                    num1 = rand.nextInt(numberRange) + 1;
//                } while (num1 == targetResult);
//
//                do {
//                    num2 = rand.nextInt(numberRange) + 1;
//                } while (num2 == targetResult);
//
//                do {
//                    num3 = rand.nextInt(numberRange) + 1;
//                } while (num3 == targetResult);
//
//                operator1 = operators[rand.nextInt(operators.length)];
//                operator2 = operators[rand.nextInt(operators.length)];
//
//                validResult1 = calculate(num1, num2, operator1);
//                validResult = calculate(validResult1, num3, operator2);
//            } while (validResult != targetResult);
//
//            // Thêm toán hạng và phép toán vào danh sách lựa chọn
//            choices.set(0, new Choice(num1));
//            choices.set(1, new Choice(operator1));
//            choices.set(2, new Choice(num2));
//            choices.set(3, new Choice(operator2));
//            choices.set(4, new Choice(num3));
//        }
//        return choices;
//    }
    private static List<Choice> ensureValidExpression(List<Choice> choices, int targetResult, Operator[] operators,
            int numberRange, int round) {
        Random rand = new Random();
        int num1, num2, num3;
        Operator operator1, operator2;

        // Tạo danh sách các số không bằng targetResult
        List<Integer> validNumbers = new ArrayList<>();
        for (int i = 1; i <= numberRange; i++) {
            if (i != targetResult) {
                validNumbers.add(i);
            }
        }

        if (round == 1) {
            // Vòng 1: 2 toán hạng và 1 phép toán
            int validResult;
            do {
                num1 = validNumbers.get(rand.nextInt(validNumbers.size()));
                num2 = validNumbers.get(rand.nextInt(validNumbers.size()));
                operator1 = operators[rand.nextInt(operators.length)];
                validResult = calculate(num1, num2, operator1);
            } while (validResult != targetResult);

            // Thêm toán hạng và phép toán vào danh sách lựa chọn
            choices.set(0, new Choice(num1));
            choices.set(1, new Choice(operator1));
            choices.set(2, new Choice(num2));

            // Sinh các số ngẫu nhiên còn lại từ validNumbers
            for (int i = 3; i < choices.size(); i++) {
                if (!choices.get(i).isIsOperator()) {
                    choices.set(i, new Choice(validNumbers.get(rand.nextInt(validNumbers.size()))));
                }
            }
        } else {
            // Vòng 2: 3 toán hạng và 2 phép toán
            int validResult, validResult1;
            do {
                num1 = validNumbers.get(rand.nextInt(validNumbers.size()));
                num2 = validNumbers.get(rand.nextInt(validNumbers.size()));
                num3 = validNumbers.get(rand.nextInt(validNumbers.size()));
                operator1 = operators[rand.nextInt(operators.length)];
                operator2 = operators[rand.nextInt(operators.length)];

                validResult1 = calculate(num1, num2, operator1);
                validResult = calculate(validResult1, num3, operator2);
            } while (validResult != targetResult || validResult1 == targetResult);

            // Thêm toán hạng và phép toán vào danh sách lựa chọn
            choices.set(0, new Choice(num1));
            choices.set(1, new Choice(operator1));
            choices.set(2, new Choice(num2));
            choices.set(3, new Choice(operator2));
            choices.set(4, new Choice(num3));

            // Sinh các số ngẫu nhiên còn lại từ validNumbers
            for (int i = 5; i < choices.size(); i++) {
                if (!choices.get(i).isIsOperator()) {
                    choices.set(i, new Choice(validNumbers.get(rand.nextInt(validNumbers.size()))));
                }
            }
        }
        return choices;
    }

    private static int calculate(int num1, int num2, Operator operator) {
        switch (operator) {
            case PLUS:
                return num1 + num2;
            case MINUS:
                return num1 - num2;
            case MULTIPLY:
                return num1 * num2;
            default:
                throw new IllegalArgumentException("Invalid operator");
        }
    }

}
