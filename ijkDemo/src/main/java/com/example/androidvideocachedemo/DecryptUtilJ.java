package com.example.androidvideocachedemo;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * @create zhl
 * @date 2023/9/6 13:49
 * @description
 * @update
 * @date
 * @description
 **/
public class DecryptUtilJ {
    private static void DecFile(File encFile, File decFile) throws IOException {
        if (!encFile.exists()) {
            System.out.println("source file not exixt");
            return;
        }
        if (!decFile.exists()) {
            int key = 200524;
            RandomAccessFile raf = null;
            RandomAccessFile raf1 = null;
            try {
                raf = new RandomAccessFile(encFile, "r");
                raf1 = new RandomAccessFile(decFile, "rw");
                int partSize = 1024 * 1024 * 2;
                int readLength;
                long currReadLength = 0;
                byte[] buffer = new byte[partSize];
                while ((readLength = raf.read(buffer)) != -1) {
                    if (readLength < partSize) {
                        //最后一次可能不满足整个buffer都有数据
                        byte[] newBuffer = new byte[readLength];
                        System.arraycopy(buffer, 0, newBuffer, 0, readLength);
                    }
                    for (int i = 0; i < buffer.length; i++) {
                        buffer[i] = (byte) (buffer[i] ^ key);
                    }
                    raf1.seek(currReadLength);
                    raf1.write(buffer, 0, buffer.length);
                    currReadLength += readLength;
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (raf != null) {
                    try {
                        raf.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (raf1 != null) {
                    try {
                        raf1.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
