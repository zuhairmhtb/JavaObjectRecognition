/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package commontools;

import java.util.Random;

/**
 *
 * @author User
 */
public class Matrix {
    public float[][] data;
    public int rows, columns;
   
    boolean print = true; 
    public Matrix(int rows, int columns){
        data = new float[rows][columns];
        this.rows = rows;
        this.columns = columns;
    }
    public void println(String text){
        if(print) System.out.println(text);
    }
    public void print(String text){
        if(print) System.out.print(text);
    }
    public void randomize(){
        Random r = new Random();
        for(int i=0; i < rows; i++){
            for(int j=0; j <columns; j++){
                int sign = -1;
                if(r.nextInt() % 2 == 0) sign = 1;
                data[i][j] = (float) r.nextFloat() * 2 - 1;
            }
        }
        
    }
    public void print(boolean print){
        if(print){
           for(int i=0; i < rows; i++){
                for(int j=0; j < columns; j++){
                    System.out.print(data[i][j] + "");
                    if(j != columns - 1) System.out.print(" ");
                }
               System.out.println("");
            } 
        }
        
    }
    public void multiply(float scalar){
        for(int i=0; i < rows; i++){
            for(int j=0; j < columns; j++){
                data[i][j] *= scalar;
            }
        }
        
    }
    public static Matrix multiply(Matrix m, float scalar){
        Matrix result = new Matrix(m.rows, m.columns);
        for(int i=0; i < m.rows; i++){
            for(int j = 0; j < m.columns; j++){
                result.data[i][j]= m.data[i][j] * scalar;
            }
        }
        return result;
    }
    public static Matrix elementwiseMultiply(Matrix m1, Matrix m2){
        if(m1.rows ==m2.rows && m1.columns == m2.columns){
            //print("Performing elementwisw matrix multiplication");
            Matrix result = new Matrix(m1.rows, m1.columns);
            //println("The output matrix has " + result.rows + " rows and " + result.columns + " columns");
            for(int i=0; i < result.rows; i++){
                for(int j=0; j<result.columns; j++){
                    result.data[i][j] = m1.data[i][j] * m2.data[i][j];
                    
                }
            }            
            return result;
        }
        return null;
    }
    public void elementwiseMultiply(Matrix m){
        if(rows == m.rows && columns == m.columns){
            print("Performing elementwise matrix multiplication");
            for(int i=0; i < rows; i++){
                for(int j=0; j<columns; j++){
                    data[i][j] = data[i][j] * m.data[i][j];
                    
                }
            }     
        }
    }
    public static Matrix rowcolumnMultiply(Matrix m1, Matrix m2){
        if(m1.columns == m2.rows){  
            //print("Performing row-column matrix multiplication");
            Matrix result = new Matrix(m1.rows, m2.columns);
            //println("The output matrix has " + result.rows + " rows and " + result.columns + " columns");
            for(int i=0; i < result.rows; i++){
                for(int j=0; j<result.columns; j++){
                    result.data[i][j] = 0;
                    for(int k=0; k < m1.columns; k++){
                        //print("Multiplying row " + i  +" and column " + k + " of first matrix by row " + k + " and column " + j + "  of second matrix and adding to row " + i + " and column " + j + " of result");
                        result.data[i][j] += m1.data[i][k] * m2.data[k][j];
                        //print(m1.data[i][k] + " * " + m2.data[k][j] + " = " + result.data[i][j]);
                    }
                }
            }            
            return result;
        }
        return null;
    }
    public void rowcolumnMultiply(Matrix m){
        if(columns == m.rows){    
            print("Performing row-column matrix multiplication");
            Matrix result = new Matrix(rows, m.columns);
            for(int i=0; i < result.rows; i++){
                for(int j=0; j < result.columns; j++){
                    result.data[i][j] = 0;
                    for(int k=0; k < columns; k++){
                        //print("Multiplying row " + i  +" and column " + k + " of first matrix by row " + k + " and column " + j + "  of second matrix and adding to row " + i + " and column " + j + " of result");
                        result.data[i][j]+=data[i][k]*m.data[k][j];
                        //print(data[i][k] + " * " + m.data[k][j] + " = " + result.data[i][j]);
                        
                    }                    
                }
            }
                  
        }
    }
    public static Matrix singleMultiply(Matrix m1, Matrix m2){
        if(m1.columns == 1 && m2.rows == 1){
            //print("Performing 1 row and 1 column matrix multiplication");
            Matrix result = new Matrix(m1.rows, m2.columns);
            for(int i=0; i < result.rows; i++){
                for(int j=0; j < result.columns; j++){                    
                    result.data[i][j] = m1.data[i][0] * m2.data[0][j];
                    //print("Multiplying " +  m1.data[i][0] + " and " + m2.data[0][j] + " which equals " + result.data[i][j]);
                }
            }
            return result;
        }
        return null;
    }
    public void singleMultiply(Matrix m){
        if(columns == 1 && m.rows == 1){
            print("Performing 1 column and 1 row matrix multiplication");
            Matrix result = new Matrix(rows, m.columns);
            for(int i=0; i < result.rows; i++){
                for(int j=0; j < result.columns; j++){
                    result.data[i][j] = data[i][0] * m.data[0][j];
                }
            }
            data = result.data;
            rows = result.rows;
            columns = result.columns;
        }
    }
    
    public void add(float scalar){
        for(int i=0; i < rows; i++){
            for(int j=0; j < columns; j++){
                data[i][j] += scalar;
            }
        }
        
    }
    public static Matrix add(Matrix m, float scalar){
        Matrix result = new Matrix(m.rows, m.columns);
        for(int i=0; i < m.rows; i++){
            for(int j=0; j < m.columns; j++){
                result.data[i][j] = m.data[i][j] + scalar;
            }
        }
        return result;
    }
    public void add(Matrix m){
        if(rows == m.rows && columns == m.columns){
            for(int i=0; i < rows; i++){
                for(int j=0; j < columns; j++){
                    data[i][j] += m.data[i][j];
                }
            }
            
        }
        else println("Unequal number of columns");
        
    }
    public static Matrix add(Matrix m1, Matrix m2){
        if(m1.rows == m2.rows && m1.columns == m2.columns){           
            Matrix result = new Matrix(m1.rows, m1.columns);
            for(int i=0; i < m1.rows; i++){
                for(int j=0; j < m2.columns; j++){
                    result.data[i][j] = m1.data[i][j] +  m2.data[i][j];
                }
            } 
            return result;
        }
        return null;
    }
    public void subtract(Matrix m){
        if(rows == m.rows && columns == m.columns){
            for(int i=0; i < rows; i++){
                for(int j=0; j < columns; j++){
                    data[i][j] -= m.data[i][j];
                }
            }
        }        
    }
    public static Matrix subtract(Matrix m1, Matrix m2){
        if(m1.rows == m2.rows && m1.columns == m2.columns){
            Matrix result = new Matrix(m1.rows, m1.columns);
            for(int i=0; i < m1.rows; i++){
                for(int j=0; j < m1.columns; j++){
                    result.data[i][j] = m1.data[i][j] - m2.data[i][j];
                }
            }
            return result;
        }
        return null;
    }
    public void transpose(){
        Matrix result = new Matrix(columns, rows);
        for(int i=0; i < rows; i++){
            for(int j=0; j < columns; j++){
                result.data[j][i] = data[i][j];
            }
        }
        data = result.data;
        rows = result.rows;
        columns = result.columns;
        
    }
    public static Matrix transpose(Matrix m){
        Matrix result = new Matrix(m.columns, m.rows);
        for(int i=0; i < m.rows; i++){
            for(int j=0; j < m.columns; j++){
                result.data[j][i] = m.data[i][j];
            }
        }
        
        return result;
    }
    public static Matrix fromArray(float[] array){
        Matrix result = new Matrix(array.length, 1);
        for(int i=0; i < array.length; i++){
            result.data[i][0] = array[i];
        }
        return result;
    }
    public static float[] toArray(Matrix m){
        float[] result = new float[m.rows * m.columns];
        int current = 0;
        for(int i=0; i < m.rows;i++){
            for(int j=0; j < m.columns; j++){
                result[current] = m.data[i][j];
                current++;
            }
        }
        return result;
    }
    
    public static void print(Matrix m, boolean print){
        if(print){
            for(int i=0; i < m.rows; i++){
                for(int j=0; j < m.columns; j++){
                    System.out.print(m.data[i][j]);
                    if(j != m.columns - 1) System.out.print(" ");
                }
                System.out.println();
            }
        }
        
    }
    public void execute(MyFloatInterface myInterface){
        
        for(int i=0; i < rows; i++){
            for(int j=0; j < columns; j++){
                data[i][j] = myInterface.execute(data[i][j], i, j);
            }
           
        }
    }
    public static Matrix execute(Matrix matrix, MyFloatInterface myInterface){
        Matrix result = new Matrix(matrix.rows, matrix.columns);
        for(int i=0; i < matrix.rows; i++){
            for(int j=0; j < matrix.columns; j++){
                result.data[i][j] = myInterface.execute(matrix.data[i][j], i, j);
            }
        }
        return result;
    }
    
    public interface MyFloatInterface{
        public float execute(float x, int i, int j);
    }
}
