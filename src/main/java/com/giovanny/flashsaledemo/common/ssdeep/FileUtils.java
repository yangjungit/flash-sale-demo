package com.giovanny.flashsaledemo.common.ssdeep;

import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.ooxml.extractor.POIXMLTextExtractor;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;



/**
 * @author YangJun
 * @description
 * @date 2020/5/29 9:36
 */
@Slf4j
public class FileUtils {
    interface FsConstant {
        String DOC_FORMAT = "doc";
        String DOCX_FORMAT = "docx";
        String PDF_FORMAT = "pdf";
        String TXT_FORMAT = "txt";
    }


    public static String getFileHash(String fileType, String path) throws IOException {
        File file = new File(path);
        return getFileHash(fileType, file);
    }


    public static String getFileHash(String fileType, File file) throws IOException {
        FileInputStream fileInputStream = new FileInputStream(file);
        return getFileHash(fileType, fileInputStream);
    }

    public static String getFileHash(String fileType, InputStream is) throws IOException {
        //创建临时文件
        File temp = File.createTempFile("pattern", ".txt");
        try {
            StringBuilder buffer = new StringBuilder();
            if (fileType.endsWith(FsConstant.DOC_FORMAT)) {
                WordExtractor ex = new WordExtractor(is);
                buffer = new StringBuilder(ex.getText());
                ex.close();
            } else if (fileType.endsWith(FsConstant.DOCX_FORMAT)) {
                try {
                    OPCPackage opcPackage = OPCPackage.open(is);
                    POIXMLTextExtractor extractor = new XWPFWordExtractor(opcPackage);
                    buffer = new StringBuilder(extractor.getText());
                    extractor.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (fileType.endsWith(FsConstant.PDF_FORMAT)) {
                try {
                    PdfReader reader = new PdfReader(is);
                    int pageNum = reader.getNumberOfPages();
                    for (int i = 1; i <= pageNum; i++) {
                        //读取第i页的文档内容
                        buffer.append(PdfTextExtractor.getTextFromPage(reader, i));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (fileType.endsWith(FsConstant.TXT_FORMAT)) {
                try (InputStreamReader isr = new InputStreamReader(is, StandardCharsets.UTF_8)) {
                    BufferedReader bufferedReader = new BufferedReader(isr);
                    String str;
                    while ((str = bufferedReader.readLine()) != null) {
                        buffer.append(str);
                    }
                } catch (Exception e) {
                    log.error("err,msg:{},cause:{}", e.getMessage(), e.getCause());
                }
            } else {
                log.info("不支持的文件类型->{}", fileType);
            }
            //获取输 出流
            BufferedWriter out = new BufferedWriter(new FileWriter(temp));
            out.write(buffer.toString());
            out.close();
            Ssdeep ssdeep = new Ssdeep();
            return ssdeep.fuzzy_hash_file(temp);
        } finally {
            temp.delete();
        }
    }


    /**
     * 魔数到文件类型的映射集合
     */
    public static final Map<String, String> TYPES = new HashMap<>();

    static {
        // 图片，此处只提取前六位作为魔数
        TYPES.put("FFD8FF", "jpg");
        TYPES.put("89504E", "png");
        TYPES.put("474946", "gif");
        TYPES.put("524946", "webp");
        TYPES.put("68656C", "txt");
        TYPES.put("D0CF11", "doc");
        TYPES.put("504B03", "docx");
        TYPES.put("255044", "pdf");
        TYPES.put("4D5A90", "exe");
    }

    /**
     * 根据文件的字节数据获取文件类型
     *
     * @param data 文件字节数组数据
     * @return
     */
    public static String getFileType(byte[] data) {
        //提取前六位作为魔数
        return getHex(data, 6);
    }

    /**
     * 获取16进制表示的魔数
     *
     * @param data              字节数组形式的文件数据
     * @param magicNumberLength 魔数长度
     * @return
     */
    public static String getHex(byte[] data, int magicNumberLength) {
        //提取文件的魔数
        StringBuilder magicNumber = new StringBuilder();
        //一个字节对应魔数的两位
        int magicNumberByteLength = magicNumberLength / 2;
        for (int i = 0; i < magicNumberByteLength; i++) {
            magicNumber.append(Integer.toHexString(data[i] >> 4 & 0xF));
            magicNumber.append(Integer.toHexString(data[i] & 0xF));
        }

        return magicNumber.toString().toUpperCase();
    }

    public static void main(String[] args) {
//        File file = new File("C:\\Users\\yj12922\\Desktop\\test\\notepad++.doc");
        try {
//            InputStream inputStream = new FileInputStream(file);
//            Path path = Paths.get("C:\\Users\\yj12922\\Desktop\\test", "notepad++.pdf");
//            String contentType = Files.probeContentType(path);
//            System.out.println("contentType=" + contentType);
//            String hash = getFileHash("doc", inputStream);
//            System.out.println("hash=" + hash);
            InputStream inputStream = new FileInputStream("C:\\Users\\yj12922\\Desktop\\test\\.gitignore");
            byte[] b = new byte[inputStream.available()];
            inputStream.read(b);
            String fileType = getFileType(b);
            System.out.println(fileType);
        } catch (IOException e) {
            log.error("......error");
            e.printStackTrace();
        }
    }
}
