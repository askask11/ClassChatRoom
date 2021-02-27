package com.classchatroom.model;

import java.awt.Color;
import java.util.Random;

/*Editor: Johnson Gao
 * Date This Project Created: Jan 28 2020
 * Description Of This Class: This class enables the app to generate random numbers,
strings, and colors.
 */
/**
 * Generating random numbers, strings, colors.
 *
 * @author Johnson Gao
 */
public class Randomizer
{

    /**
     * Produce a random integer that is lower than upperbound and greater than
     * lower bound..
     *
     * @param lowerBound lowerbound of the int.
     * @param upperBound Upperbound of the int.
     * @return a random int.
     */
    public static int randomInt(int lowerBound, int upperBound)
    {
        return lowerBound + (int) (Math.random() * (upperBound - lowerBound + 1));
    }

    /**
     * Produce a random double that is greater than lower bound but lower than
     * the upper bound.
     *
     * @param lowerBound The minimum of the random double.
     * @param upperBound The maximum of the random double.
     * @return The random double produced.
     */
    public static double randomDouble(double lowerBound, double upperBound)
    {
        return lowerBound + (Math.random() * (upperBound - lowerBound + 1));
    }

    /**
     * Produce a random double within the lowerbound and upperBound, and it will
     * be round within any digits assigned.
     *
     * @param lowerBound LowerBound of the new random double.
     * @param upperBound UpperBound of the new random double.
     * @param round Round to how many digits.
     * @return The new random double.
     */
    public static double randomDouble(double lowerBound, double upperBound, int round)
    {
        return Double.parseDouble(String.format("%." + round + "f", randomDouble(lowerBound, upperBound)));
    }

    /**
     * Return a sequence of random number with specific length.
     * <br>If the upperbound is greater than 9, the returned number may be
     * longer than expected. Therefore the lowerbound and upperbound must be
     * within 0-9;
     *
     * @param lowerBound Lowerbound of the new number.
     * @param upperBound Upperbound
     * @param length The length.
     * @return A sequence of random number.
     * @throws IllegalArgumentException If the lowerbound or upperbound is not
     * within 0-9;
     */
    public static String randomStringId(int lowerBound, int upperBound, int length)
    {
        lengthRangeCheck(length);
        String id = "";
        if (lowerBound >= 0 && upperBound <= 9)
        {
            for (int i = 0; i < length; i++)
            {
                id = id.concat(Integer.toString(randomInt(lowerBound, upperBound)));
            }
        } else
        {
            throw new IllegalArgumentException("Lower bound and upper bound must be within 0-9, "
                    + "\n Lowerbound=" + lowerBound
                    + "\n Upperbound=" + upperBound);
        }
        return id;
    }

    /**
     * Return a sequence of random number with specific length.
     *
     * @param length The length of the group of the random number, which is the
     * String that will be returned.
     * @return A random group of number group of number in String.
     */
    public static String randomStringId(int length)
    {
        return randomStringId(0, 9, length);
    }

    /**
     * Produce a group of random numbers and letters.
     *
     * @param length The length of the new string identical number sequence.
     * @param isCaseRandom If randomize the case of the chars given.
     * @param letters A set of chars that can appear in the random sequence generated.
     * @return A new group of random letters and numbers.
     */
    public static String randomLetterNumber(int length, boolean isCaseRandom, char... letters)
    {
        lengthRangeCheck(length);
        String id = "";
        int nextType;
        for (int i = 0; i < length; i++)
        {
            nextType = randomInt(0, 1);
            if (nextType == 0)
            {
                id += randomInt(0, 9);//Assign a number in the end of the string(concat).
            } else
            {
                char nowChar = letters[randomInt(0, letters.length - 1)];
                if (isCaseRandom)
                {
                    int cases = randomInt(0, 1);
                    if (cases == 0)
                    {
                        nowChar = Character.toLowerCase(nowChar);
                    } else
                    {
                        nowChar = Character.toUpperCase(nowChar);
                    }
                }
                id += nowChar;//Assign the new char to the end of the string.
            }
        }
        return id;
    }

    /**
     * Generate a random letter within given a char array.
     *
     * @param letters
     * @return
     */
    public static char randomLetter(char[] letters)
    {
        return letters[randomInt(0, letters.length - 1)];
    }

    /**
     * Genetate given length of letters.
     *
     * @param letters
     * @param length
     * @return
     */
    public static String randomLetters(char[] letters, int length)
    {
        String letter = "";
        if (length < 0)
        {
            throw new IllegalArgumentException("length cannot be negative. For length= " + length);
        }

        for (int i = 0; i < length; i++)
        {
            letter += randomLetter(letters);
        }
        return letter;
    }

    /**
     * Check the range of the length.
     *
     * @param length The length of the number.
     */
    private static void lengthRangeCheck(int length)
    {
        if (length <= 0)
        {
            throw new IllegalArgumentException("Length given = " + length + " must be greater than 0!");
        }
    }

    public static String generateBash()
    {
        String bash = Randomizer.randomLetterNumber(15, true, "QWERTYUIOPASDFGHJKLZXCVBNM".toCharArray());
        return bash;
    }
    
    
    static Color getRandomColor(int fc, int bc) {
        Random random = new Random();
        if (fc > 255) fc = 255;
        if (bc > 255) bc = 255;
        int r = fc + random.nextInt(bc - fc);
        int g = fc + random.nextInt(bc - fc);
        int b = fc + random.nextInt(bc - fc);
        return new Color(r, g, b);
    }
    /**
     * Test main method.
     *
     * @param args Lines command argument.
     */
    public static void main(String[] args)
    {
        //System.out.println(randomLetters(CipherFactory.ALL_ALPHABETS_UPPERCASE, 30));
        //System.out.println("journeylove.Randomizer.main()");
        System.out.println(randomInt(-1000, 1000));
    }
}
