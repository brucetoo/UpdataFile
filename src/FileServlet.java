import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.List;

/**
 * Created by Bruce
 * Data 2014/8/11
 * Time 14:40.
 */
public class FileServlet extends javax.servlet.http.HttpServlet {


    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        // 创建文件项目工厂对象
        DiskFileItemFactory factory = new DiskFileItemFactory();

        // 设置文件上传路径
        String upload = this.getServletContext().getRealPath("/upload/");
        System.out.println("-----------------------------------"+upload);
        // 获取系统默认的临时文件保存路径，该路径为Tomcat根目录下的temp文件夹
        String temp = System.getProperty("java.io.tmpdir");
        // 设置缓冲区大小为 5M
        factory.setSizeThreshold(1024 * 1024 * 5);
        // 设置临时文件夹为temp
        factory.setRepository(new File(temp));
        // 用工厂实例化上传组件,ServletFileUpload 用来解析文件上传请求
        ServletFileUpload servletFileUpload = new ServletFileUpload(factory);

        // 解析结果放在List中
        try
        {
            List<FileItem> list = servletFileUpload.parseRequest(request);

            for (FileItem item : list)
            {
                String name = item.getFieldName();
                InputStream is = item.getInputStream();

                if (name.contains("content"))
                {
                    System.out.println(inputStream2String(is));
                } else if(name.contains("file"))
                {
                    try
                    {
                        inputStream2File(is, upload + "\\" + item.getName());
                    } catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
            }

            out.write("success");
        } catch (FileUploadException e)
        {
            e.printStackTrace();
            out.write("failure");
        }

        out.flush();
        out.close();
    }


    // 流转化成字符串
    public static String inputStream2String(InputStream is) throws IOException
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int i = -1;
        while ((i = is.read()) != -1)
        {
            baos.write(i);
        }
        return baos.toString();
    }


    // 流转化成文件
    public static void inputStream2File(InputStream is, String savePath)
            throws Exception
    {
        System.out.println("文件保存路径为:" + savePath);
        File file = new File(savePath);
        if(!file.exists()){
            file.mkdirs();// 目录不存在的情况下，创建目录。
        }
        InputStream inputSteam = is;
        BufferedInputStream fis = new BufferedInputStream(inputSteam);
        FileOutputStream fos = new FileOutputStream(file);
        int f;
        while ((f = fis.read()) != -1)
        {
            fos.write(f);
        }
        fos.flush();
        fos.close();
        fis.close();
        inputSteam.close();

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }
}
