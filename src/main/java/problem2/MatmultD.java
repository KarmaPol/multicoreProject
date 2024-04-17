package problem2;

import java.util.*;
import java.lang.*;
import java.util.concurrent.atomic.AtomicInteger;

// command-line execution example) java MatmultD 6 < mat500.txt
// 6 means the number of threads to use
// < mat500.txt means the file that contains two matrices is given as standard input
//
// In eclipse, set the argument value and file input by using the menu [Run]->[Run Configurations]->{[Arguments], [Common->Input File]}.

// Original JAVA source code: http://stackoverflow.com/questions/21547462/how-to-multiply-2-dimensional-arrays-matrix-multiplication
public class MatmultD
{
  private static Scanner sc = new Scanner(System.in);
  private static AtomicInteger[][] matrixC;
  private static AtomicInteger sum = new AtomicInteger(0);

  public static void main(String [] args) throws InterruptedException {
    int thread_no=0;
    if (args.length==1) thread_no = Integer.valueOf(args[0]);
    else thread_no = 1;

    int a[][]=readMatrix();
    int b[][]=readMatrix();

    int MatrixRowLength = a.length;
    int MatrixColLength = b[0].length;

    matrixC = new AtomicInteger[MatrixRowLength][MatrixColLength];
    for (int i = 0; i < MatrixRowLength; i++) {
      for (int j = 0; j < MatrixColLength; j++) {
        matrixC[i][j] = new AtomicInteger();
      }
    }

    long startTime = System.currentTimeMillis();
    
    List<LoadBalancerStaticBlock.Section> sections = LoadBalancerStaticBlock.getSections(thread_no, MatrixRowLength);

    Thread[] threads = new Thread[thread_no];

    for(int i = 0; i < thread_no; i++) {
      int start = sections.get(i).start;
      int end = sections.get(i).end;
      MultMatrixThread mt = new MultMatrixThread(i, a, b, start, end);
      threads[i] = mt;
      mt.start();
    }

    for(Thread mt : threads) {
      mt.join();
    }
    long endTime = System.currentTimeMillis();

    //printMatrix(a);
    //printMatrix(b);    
    // printMatrix(convertAtomicIntegerMatrixToIntMatrix());

    //System.out.printf("thread_no: %d\n" , thread_no);
    //System.out.printf("Calculation Time: %d ms\n" , endTime-startTime);

    System.out.printf("[thread_no]:%2d , [Time]:%4d ms\n", thread_no, endTime-startTime);
    System.out.println("Matrix Sum = " + sum.get() + "\n");
  }

  public static int[][] convertAtomicIntegerMatrixToIntMatrix() {
    if (matrixC == null) {
      return null; // null 입력에 대한 처리
    }

    int rows = matrixC.length; // 행의 수
    if (rows == 0) {
      return new int[0][0]; // 입력 행렬이 비어있는 경우
    }

    int cols = matrixC[0].length; // 열의 수, 모든 행이 동일한 길이를 가정
    int[][] intMatrix = new int[rows][cols];

    for (int i = 0; i < rows; i++) {
      if (matrixC[i] != null) {
        for (int j = 0; j < cols; j++) {
          // AtomicInteger 값을 int 값으로 변환하여 새 행렬에 할당
          intMatrix[i][j] = matrixC[i][j].get();
        }
      }
    }

    return intMatrix;
  }

   public static int[][] readMatrix() {
       int rows = sc.nextInt();
       int cols = sc.nextInt();
       int[][] result = new int[rows][cols];
       for (int i = 0; i < rows; i++) {
           for (int j = 0; j < cols; j++) {
              result[i][j] = sc.nextInt();
           }
       }
       return result;
   }

  public static void printMatrix(int[][] mat) {
  System.out.println("Matrix["+mat.length+"]["+mat[0].length+"]");
    int rows = mat.length;
    int columns = mat[0].length;
    int sum = 0;
    for (int i = 0; i < rows; i++) {
      for (int j = 0; j < columns; j++) {
        System.out.printf("%4d " , mat[i][j]);
        sum+=mat[i][j];
      }
      System.out.println();
    }
    System.out.println();
    System.out.println("Matrix Sum = " + sum + "\n");
  }

  static class LoadBalancerStaticBlock {
    static List<Section> getSections(int thread_no, int matrix_length) {
      List<Section> sections = new ArrayList<>();
      int offset = matrix_length/thread_no;
      for(int i = 0; i < thread_no; i++) {
        int start = i * offset;
        int end = (i+1) * offset;
        if(i == thread_no - 1) {
          end = matrix_length;
        }
        Section section = new Section(start, end);
        sections.add(section);
      }
      return sections;
    }

    static class Section {
      int start;
      int end;
      public Section(int start, int end) {
        this.start = start;
        this.end = end;
      }
    }
  }

  static class MultMatrixThread extends Thread {
    int taskNumber;
    int[][] a;
    int[][] b;
    int start;
    int end;

    MultMatrixThread(int taskNumber, int[][] a, int[][] b, int start, int end) {
      this.taskNumber = taskNumber;
      this.a = a;
      this.b = b;
      this.start = start;
      this.end = end;
    }

    public void run() {
      long startTime = System.currentTimeMillis();
      for(int i = start; i < end; i++) {
        for(int j = 0; j < b[0].length; j++) {
          for(int k = 0; k < a[0].length; k++) {
            matrixC[i][j].addAndGet(a[i][k] * b[k][j]);
          }
          sum.addAndGet(matrixC[i][j].get());
        }
      }
      long endTime = System.currentTimeMillis();
      long duration = endTime - startTime;
      System.out.println("[thread #" + taskNumber + "] completed in " + duration + " ms");
    }
  }
}
