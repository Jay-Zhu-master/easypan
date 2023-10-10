package com.jayzhu.easypan.entity.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang3.ArrayUtils;

@AllArgsConstructor
public enum FileTypeEnum {
    VIDEO(FileCategoryEnum.VIDEO, 1, new String[]{".mp4", ".MP4", ".avi", ".AVI", ".mkv", ".MKV", ".mov", ".MOV",
            ".wmv", ".WMV", ".flv", ".FLV", ".3gp", ".3GP", ".mpeg", ".MPEG", ".rmvb", ".RMVB", ".webm", ".WEBM", ".ts",
            ".TS", ".ogg", ".OGG", ".vob", ".VOB", ".dv", ".DV", ".swf", ".SWF", ".mpg", ".MPG", ".asf", ".ASF", ".qt",
            ".QT", ".m4v", ".M4V", ".m2ts", ".M2TS"}, "视频"),
    MUSIC(FileCategoryEnum.MUSIC, 2, new String[]{".mp3", ".MP3", ".wav", ".WAV", ".ogg", ".OGG", ".aac", ".AAC",
            ".flac", ".FLAC", ".wma", ".WMA", ".m4a", ".M4A", ".aiff", ".AIFF", ".ape", ".APE", ".au", ".AU", ".mid",
            ".MID", ".midi", ".MIDI", ".amr", ".AMR", ".ra", ".RA", ".ac3", ".AC3", ".dts", ".DTS", ".opus", ".OPUS",
            ".pcm", ".PCM"}, "音频"),
    IMAGE(FileCategoryEnum.IMAGE, 3, new String[]{".jpg", ".JPG", ".jpeg", ".JPEG", ".png", ".PNG", ".gif", ".GIF",
            ".bmp", ".BMP", ".tiff", ".TIFF", ".webp", ".WEBP", ".svg", ".SVG", ".ico", ".ICO", ".eps", ".EPS"
            , ".psd", ".PSD", ".ai", ".AI", ".indd", ".INDD", ".raw", ".RAW", ".tif", ".TIF", ".jfif", ".JFIF",
            ".heif", ".HEIF", ".bat", ".BAT"}, "图片"),
    PDF(FileCategoryEnum.DOC, 4, new String[]{".pdf", ".PDF"}, "pdf"),
    WORD(FileCategoryEnum.DOC, 5, new String[]{".doc", ".DOC", ".docx", ".DOCX"}, "word"),
    EXCEL(FileCategoryEnum.DOC, 6, new String[]{".xls", ".xlsx", ".XLS", ".XLSX"}, "excel"),
    TXT(FileCategoryEnum.DOC, 7, new String[]{".txt", ".TXT"}, "txt文本"),
    PROGRAM(FileCategoryEnum.OTHERS, 8, new String[]{".c", ".C", ".h", ".H", ".cpp", ".CPP", ".cxx", ".CXX",
            ".cc", ".CC", ".java", ".JAVA", ".py", ".PY", ".js", ".JS", ".php", ".PHP", ".html", ".HTML", ".css",
            ".CSS", ".xml", ".XML", ".json", ".JSON", ".sql", ".SQL", ".rb", ".RB", ".swift", ".SWIFT", ".go",
            ".GO", ".rust", ".RUST", ".perl", ".PERL", ".lua", ".LUA", ".r", ".R", ".matlab", ".MATLAB", ".scala",
            ".SCALA", ".kotlin", ".KOTLIN", ".dart", ".DART", ".typescript", ".TYPESCRIPT", ".bash", ".BASH",
            ".powershell", ".POWERSHELL", ".sh", ".SH", ".pl", ".PL", ".cobol", ".COBOL", ".fortran", ".FORTRAN",
            ".assembly", ".ASSEMBLY"}, "CODE"),
    ZIP(FileCategoryEnum.OTHERS, 9, new String[]{".zip", ".ZIP", ".rar", ".RAR", ".7z", ".7Z", ".tar", ".TAR",
            ".gz", ".GZ", ".bz2", ".BZ2", ".xz", ".XZ", ".z", ".Z", ".lz", ".LZ", ".cab", ".CAB", ".iso", ".ISO",
            ".tar.gz", ".TAR.GZ", ".tar.bz2", ".TAR.BZ2", ".tar.xz", ".TAR.XZ", ".tgz", ".TGZ", ".tbz2", ".TBZ2",
            ".txz", ".TXZ", ".war", ".WAR", ".jar", ".JAR", ".zipx", ".ZIPX", ".ace", ".ACE", ".arj", ".ARJ", ".lzh",
            ".LZH", ".zoo", ".ZOO", ".sit", ".SIT", ".pkg", ".PKG", ".deb", ".DEB", ".rpm", ".RPM"}, "压缩包"),
    OTHERS(FileCategoryEnum.OTHERS, 10, new String[]{}, "其它");
    private final FileCategoryEnum category;
    private final Integer type;
    private final String[] suffixes;
    private final String desc;

    public static FileTypeEnum getFileTypeBySuffix(String suffix) {
        for (FileTypeEnum item : FileTypeEnum.values()) {
            if (ArrayUtils.contains(item.getSuffixes(), suffix)) {
                return item;
            }
        }
        return FileTypeEnum.OTHERS;
    }

    public FileCategoryEnum getCategory() {
        return category;
    }

    public Integer getType() {
        return type;
    }

    public String[] getSuffixes() {
        return suffixes;
    }

    public String getDesc() {
        return desc;
    }
}
