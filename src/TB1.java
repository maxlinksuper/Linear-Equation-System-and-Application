/* NOTE :
    - Untuk sementara, interpolasi dari keyboard belum dibuat. Dari file bisa dipakai karena format datanya sama
    - Keliatannya perlu refaktoring di akhir karena ada 3 menu utama (Menu interpolasi/SPL, Menu pembacaa file, menu Gauss-Jordan/Gauss)
*/

import java.io.*;
import java.util.Scanner;

public class TB1{
    // Untuk sementara atribut scopenya protected biar gampang ceknya, sebaiknya di akhir diganti ke private
    protected double[][] matriks;
    protected int m;
    protected int n;
    
    public TB1(int m, int n, double[][] matriks){
    // Konstruktor untuk kelas SPL
        this.matriks = matriks;
        this.m = m;
        this.n = n;
    }

    public static double[][] convertStringToDouble(String[][] dataString){
    // Fungsi untuk mengubah matriks string menjadi matriks double
        double[][] dataDouble = new double[dataString.length][(dataString[dataString.length-1]).length];

        for (int i = 0; i < dataDouble.length; i++){
            for (int j = 0; j < dataDouble[i].length; j++){
                dataDouble[i][j] = Double.parseDouble(dataString[i][j]);
            }
        }

        return dataDouble;
    }

    public static String[][] parseData(String lines){
    // Fungsi untuk melakukan parsing matriks yang diinput baik dari file maupun dari keyboard

        // Untuk antar baris digunakan delimiter \0, untuk antar kolom digunakan delimiter spasi
        String[] parsedText = lines.split("\0");
        String[] tempData = parsedText[parsedText.length-parsedText.length].split(" ");

        // Variabel tempMatriks = variabel yang berisi matriks string yang akan di return
        String[][] tempMatriks = new String[parsedText.length][tempData.length];
        int i = 0;

        // Iterasi setiap baris string yang sudah di split berdasarkan \0 untuk di split berdasarkan spasi
        // Substring ini kemudian dimasukkan ke kolom-kolom tempMatriks
        for (String temp : parsedText){
            String[] temp2 = temp.split(" ");

            for (int j = 0; j < temp2.length; j++){
                tempMatriks[i][j] = temp2[j];
            }
            i++;
        }

        return tempMatriks;
    }

    public static String getInput(int type){
    // Mengambil input dari pengguna berdasarkan pilihan, 1 = SPL, 2 = Interpolasi
        Scanner scanner = new Scanner(System.in);

        System.out.print("METODE INPUT\n" +
            "1. Dari file\n" +
            "2. Dari keyboard\n" +
            "3. Kembali\n");

        String lines = "";
        while (true){
            System.out.print("Pilihan menu (1/2/3) : ");
            int input = scanner.nextInt();

            System.out.println("\n\n");

            if (input == 1){    // Input interpolasi dan SPL dari file sama, bisa digabungkan
                while (true){
                    try{
                        // Mencoba untuk membuka file berdasarkan input pengguna
                        System.out.print("Masukkan nama file : ");
                        String filename = scanner.next();
                        BufferedReader bf = new BufferedReader(new FileReader(filename));
                        
                        // Membaca file eksternal hingga habis (null). Hasil pembacaan di-append ke variabel lines
                        String line = bf.readLine();
                        if (((type == 1) && ((line.split(" ")).length < 3)) || ((type == 2) && ((line.split(" ")).length > 2))){
                        // Bisa saja pilihan pengguna tidak sama dengan file yang dimasukkan
                        // Dalam hal ini apabila :
                        //  - Pengguna memilih SPL tapi jumlah variabel dalam 1 baris kurang dari 3 maka file tersebut adalah interpolasi
                        //  - Pengguna memilih interpolasi tapi jumlah variabel dalam 1 baris lebih dari 2 maka file tersebut adalah SPL
                            bf.close();
                            throw (new DataTypeException());  // Isi file tidak sesuai dengan pilihan, throw exception
                        }

                        while (line != null){
                            lines += line;
                            
                            if ((line = bf.readLine()) != null){
                                lines += "\0";
                            }
                        }

                        bf.close();
                        break;
                    } catch (FileNotFoundException e){  // File tidak ada
                        System.out.println("ERROR Tidak ada file dengan nama tersebut.");
                    } catch (DataTypeException e){  // File ada tapi isinya tidak sesuai dengan pilihan
                        System.out.println("ERROR Isi file tidak sesuai dengan pilihan.");
                    } catch (IOException e){    // Ada masalah terkait pembacaan file
                        System.out.println("ERROR Ada masalah dengan pembacaan file.");
                    }
                }
                break;
            } else if (input == 2){ // Input dari keyboard
                switch (type){  // Karena bentuk input berbeda, harus dipisah berdasarkan pilihan pengguna (SPL atau interpolasi)
                    case 1:  //SPL
                        Scanner scanner2 = new Scanner(System.in).useDelimiter("x");

                        // Selama dimensi yang diinput dan matriks yang diinput tidak sama, akan terus mengulang
                        while(true){
                            System.out.print("Please enter the matrix dimension (in MxN) : ");
                            String[] temp = (scanner2.nextLine()).split("x");
                            int m = Integer.parseInt(temp[0]);
                            int n = Integer.parseInt(temp[1]);

                            System.out.println("Now enter your matrix. Split every element in the same row with a space.");
                            // Membaca matriks hingga m baris
                            while (m > 0){
                                lines += scanner2.nextLine();

                                m--;
                                if (m > 0){
                                    lines += '\0';
                                }
                            }
                            // Reset nilai m
                            m = Integer.parseInt(temp[0]);

                            // Membandingkan dimensi yang diinput dengan dimensi asli matriks yang diinput
                            int input_m = (lines.split("\0")).length;
                            int input_n = (((lines.split("\0"))[0]).split(" ")).length;

                            // Jika dimensi tidak sama, diulang. Jika sama keluar dari loop.
                            if ((m != input_m) || (n != input_n)){
                                System.out.println("ERROR Matrix is not the same as the dimension");
                            } else {
                                break;
                            }
                        }
                        break;
                    case 2: //Interpolasi
                        System.out.print("Masukkan banyaknya jumlah titik n : ");
                        int n = scanner.nextInt();

                        System.out.println("Sekarang masukkan nilai x dan y dengan format\nx y");
                        scanner.nextLine(); // Menghilangkan spasi di awal pembacaan
                        for (int i = 0; i < n; i++){
                        // Membaca titik hingga n titik
                            lines += scanner.nextLine();

                            if (i < n){
                                lines += '\0';
                            }
                        }
                        break;
                }
                break;
            } else if (input == 3){ // Pilihan kembali
                //Nothing
                break;
            } else {    // Pilihan tidak ada
                System.out.println("ERROR Pilihan menu salah.");
            }
        }

        return lines;
    }

    public static void main(String[] args){
        // Inisialisasi objek SPL. Wajib diinisalisasi sebelum digunakan dibawah
        TB1 mainTB1 = new TB1(0,0,null);

        String lines = "";

        while (true){
            System.out.print("MENU UTAMA\n" +
                "1. Sistem Persamaan Linier\n" +
                "2. Interpolasi Polinom\n" +
                "3. Keluar\n" +
                "Pilihan menu (1/2/3) : ");
            Scanner scanner = new Scanner(System.in);
            int input = scanner.nextInt();

            System.out.println("\n\n");

            switch (input){
                case 1:
                    lines = getInput(1);
                    break;
                case 2:
                    lines = getInput(2);
                    break;
                case 3:
                    System.exit(0);
                    break;
                default:
                    System.out.println("ERROR Pilihan menu salah.");
                    break;
            }

            if (!(lines.equals(""))){
                break;
            }
        }

        String[][] matriksData = parseData(lines);
        mainTB1 = new TB1(matriksData.length,(matriksData[matriksData.length-1]).length,convertStringToDouble(matriksData));

        for (int i = 0; i < mainTB1.m; i++){
            for (int j = 0; j < mainTB1.n; j++){
                System.out.print((mainTB1.matriks)[i][j] + " ");
            }
            System.out.println();
        }
    }
}

class DataTypeException extends IOException{
    public DataTypeException(){
        super();
    }
}
