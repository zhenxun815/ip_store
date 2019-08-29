package com.tqhy.ip_store.utils;

import com.tqhy.ip_store.configs.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.Nullable;

import java.io.*;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * @author Yiheng
 * @create 2019/1/13
 * @since 1.0.0
 */
public class FileUtils {

    static Logger logger = LoggerFactory.getLogger(FileUtils.class);


    public static Map<String, String> readFileByLinesToMap(String fileName, String encoding) {
        Map<String, String> retMap = new HashMap<String, String>();
        // File file = new File(fileName);
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(new FileInputStream(fileName), encoding));

            String tempString = null;
            int line = 1;
            while ((tempString = reader.readLine()) != null) {
                String key = tempString.substring(tempString.lastIndexOf("/") + 1);
                if (!retMap.containsKey(key)) {
                    retMap.put(key, tempString);
                }
                line++;
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                }
            }
        }
        return retMap;
    }

    public static void countMap() {
        System.out.println("start count...");
        Map<String, String> zlxmap = readFileByLinesToMap("/home/tqhy/zlx.txt", "utf-8");
        Map<String, String> fulltxtmap = readFileByLinesToMap("/home/tqhy/fulltext.txt", "utf-8");
        int countxml = 0;
        int allSize = zlxmap.size();
        int okCount = 0;
        for (
                Map.Entry<String, String> entry : zlxmap.entrySet()) {
            if (fulltxtmap.containsKey(entry.getKey())) {
                okCount++;
            }
            countxml++;
            if (countxml % 10000 == 0) {
                System.out.println("allSize:" + allSize + " count:" + countxml + " okCount:" + okCount);
            }
        }

        System.out.println("All is finished.");
    }

    /**
     * 根据后缀获取文件
     *
     * @param dir
     * @param suffix
     */
    public static List<File> getAllFilesInDir(File dir, String suffix) {

        File[] files = dir.listFiles();
        ArrayList<File> collect = Arrays.stream(files)
                                        .collect(ArrayList::new, (list, file) -> {
                                            if (file.isFile() && file.getName().endsWith(suffix)) {
                                                list.add(file);
                                            } else if (file.isDirectory()) {
                                                List<File> filesInSubDir = getAllFilesInDir(file, suffix);
                                                list.addAll(filesInSubDir);
                                            }
                                        }, ArrayList::addAll);
        return collect;
    }

    /**
     * 获取文件夹下所有文件,返回以文件名为key,文件File对象为value的map
     *
     * @param dir
     * @return
     */
    public static Map<String, File> getFileMap(File dir) {
        File[] allFiles = dir.listFiles(File::isFile);
        HashMap<String, File> fileHashMap = Arrays.stream(allFiles)
                                                  .collect(HashMap::new, (map, file) -> map.put(file.getName(), file), HashMap::putAll);
        return fileHashMap;
    }


    public static boolean copyFile(File sourceFile, File destFile) {

        logger.info("source file: " + sourceFile.getAbsolutePath() + ", dest file: " + destFile.getAbsolutePath());
        if (!sourceFile.exists()) {
            return false;
        }
        if (destFile.exists()) {
            boolean delete = destFile.delete();
            if (!delete) {
                return false;
            }
        }

        try {
            File parentFile = destFile.getParentFile();
            if (!parentFile.exists()) {
                boolean mkdirs = parentFile.mkdirs();
                if (!mkdirs) {
                    return false;
                }
            }
            boolean newFile = destFile.createNewFile();
            if (!newFile) {
                return false;
            }
            try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(sourceFile))) {
                try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(destFile))) {
                    byte[] buffer = new byte[1024 * 8];
                    int i = 0;
                    while ((i = bis.read(buffer)) != -1) {
                        bos.write(buffer);
                    }
                    bos.flush();
                    return true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 创建新文件
     *
     * @param file
     * @return
     */
    public static boolean createNewFile(File file) {
        if (file.exists()) {
            file.delete();
        }

        try {
            File parentFile = file.getParentFile();
            if (!parentFile.exists()) {
                parentFile.mkdirs();
            }
            boolean newFile = file.createNewFile();
            return newFile;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }

    /**
     * 记录错误信息到文件
     *
     * @param info
     * @param wrongFile
     */
    public static void storeWrongInfo(String info, File wrongFile) {
        if (null == wrongFile) {
            return;
        }
        writeFile(wrongFile, info, builder -> builder.append(Constants.NEW_LINE), true, true);
    }

    public static long countLine(File file, @Nullable Predicate<String> filter) {
        long lineCount = 0L;
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            lineCount = null == filter
                    ? reader.lines().count()
                    : reader.lines().filter(filter).count();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return lineCount;
    }

    /**
     * 按行读取文件,返回一个由每行处理结果对象组成的集合
     *
     * @param file     待读行文件
     * @param function 对每一行内容处理的{@link Function<String,T> Function}
     * @param <T>      返回集合泛型
     * @return
     */
    public static <T> List<T> readLine(File file, Function<String, T> function) {
        ArrayList<T> list = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            reader.lines()
                  .forEach(line -> {
                      T apply = function.apply(line);
                      if (null != apply) {
                          list.add(apply);
                      }
                  });
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 按行读取文件,并将每行内容按分隔符分隔后处理,返回一个由每行处理结果对象组成的集合
     *
     * @param file     待读行文件
     * @param function 对每一行内容处理的{@link Function<String[],T> Function}
     * @param <T>      返回集合泛型
     * @return
     */
    public static <T> List<T> readLine(File file, String splitReg, Function<String[], T> function) {
        ArrayList<T> list = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            reader.lines()
                  .forEach(line -> {
                      String[] split = line.split(splitReg);
                      if (null != split && split.length > 0) {
                          T apply = function.apply(split);
                          list.add(apply);
                      }
                  });
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 按行读取文件并消费每行内容
     *
     * @param file
     * @param consumer
     */
    public static void readLine(File file, Consumer<String> consumer) {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            reader.lines()
                  .forEach(line -> consumer.accept(line.trim()));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 按行读取文件并消费每行内容
     *
     * @param file
     * @param splitReg
     * @param consumer
     */
    public static void readLine(File file, String splitReg, Consumer<String[]> consumer) {

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            reader.lines()
                  .forEach(line -> {
                      String[] split = line.split(splitReg);
                      if (null != split && split.length > 1) {
                          consumer.accept(split);
                      }
                  });
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * 向文件追加内容,内部调用{@link FileUtils#writeFile(File, String, Function, boolean, boolean) writeFile} 方法
     *
     * @param file
     * @param info     待写入信息
     * @param consumer 处理写入内容
     * @param create   当文件不存在时是否创建新文件
     */
    public static void appendFile(File file, String info, @Nullable Function<StringBuilder, StringBuilder> consumer, boolean create) {
        writeFile(file, info, consumer, create, true);
    }

    /**
     * 写文件
     *
     * @param file
     * @param info     待写入信息
     * @param function 处理写入内容,为null则不做任何处理
     * @param create   当文件不存在时是否创建新文件
     * @param append   是否追加写入
     */
    public static void writeFile(File file, String info, @Nullable Function<StringBuilder, StringBuilder> function, boolean create, boolean append) {

        if (create && !file.exists()) {
            createNewFile(file);
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, append))) {
            if (null == function) {
                writer.append(info);
            } else {
                String apply = function.apply(new StringBuilder(info))
                                       .toString();
                writer.append(apply);
            }
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private FileUtils() {
    }

    public static FileUtils getInstance() {
        return Holder.INSTANCE;
    }

    private static class Holder {
        private static final FileUtils INSTANCE = new FileUtils();
    }
}
