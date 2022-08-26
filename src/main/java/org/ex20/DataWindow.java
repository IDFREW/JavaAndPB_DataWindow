package org.ex20;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.ArrayList;

/**
 * <p>创建时间: 2022年08月26日 14:35 </p>
 * 数据窗口生成
 * @author 高诚政
 */
public class DataWindow {
    /**
     * Guid风格的数据窗口生成
     */
    public static void DataWindowGuid(Connection conn){
        /* 拼凑SQL */
        String sql = JdbcUtil.SQL + JdbcUtil.TABLENAME;
        /* 获取主键 */
        ArrayList<String> keys = DbKeySet.keysSet(conn);
        /* 字段列名集合 */
        ArrayList<String> fieldName = new ArrayList<>();
        /* 表头字段集合 */
        ArrayList<String> columns = new ArrayList<>();
        /* 字段长度集合 */
        ArrayList<Integer> precisions = new ArrayList<>();

        PreparedStatement stmt;
        StringBuilder pBSql = new StringBuilder("retrieve=\"select ");
        try {
            stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery(sql);
            ResultSetMetaData data = rs.getMetaData();
            for (int i = 1; i <= data.getColumnCount(); i++) {
                /* 或者字段名 */
                String columnName = data.getColumnName(i);
                /* 获取字段名的类型 */
                String columnTypeName = data.getColumnTypeName(i);
                /* 获取字段的长度 */
                int precision = data.getPrecision(i);
                if("INTEGER".equals(columnTypeName)||"NUMBER".equals(columnTypeName)){
                    precisions.add(0);
                }else{
                    precisions.add(precision);
                }

                // 生成DataWindow
                String dbName = JdbcUtil.TABLENAME + "." + columnName;
                fieldName.add(columnName.toLowerCase());
                pBSql.append(columnName).append(",");
                if ("VARCHAR2".equals(columnTypeName)) {
                    columnTypeName = "CHAR";
                }
                boolean b = false;
                for (String key : keys) {
                    if (columnName.equals(key)) {
                        b = true;
                        break;
                    }
                }
                if (b) {
                    if ("NUMBER".equals(columnTypeName)) {
                        columns.add("column=(type=" + columnTypeName + " update=yes updatewhereclause=yes key=yes name=" + columnName + " dbname=\"" + dbName + "\")");
                        continue;
                    }
                    if ("INTEGER".equals(columnTypeName)) {
                        columns.add("column=(type=decimal(0) update=yes updatewhereclause=yes key=yes name=" + columnName + " dbname=\"" + dbName + "\")");
                        continue;
                    }
                    columns.add("column=(type=" + columnTypeName + "(" + precision + ") update=yes updatewhereclause=yes key=yes name=" + columnName + " dbname=\"" + dbName + "\")");
                    continue;
                }
                if ("NUMBER".equals(columnTypeName)) {
                    columns.add("column=(type=" + columnTypeName + " update=yes updatewhereclause=yes name=" + columnName + " dbname=\"" + dbName + "\")");
                    continue;
                }
                if ("INTEGER".equals(columnTypeName)) {
                    columns.add("column=(type=decimal(0) update=yes updatewhereclause=yes name=" + columnName + " dbname=\"" + dbName + "\")");
                    continue;
                }
                columns.add("column=(type=" + columnTypeName + "(" + precision + ") update=yes updatewhereclause=yes name=" + columnName + " dbname=\"" + dbName + "\")");
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            JdbcUtil.closeConnection(conn);
        }
        StringBuilder data = new StringBuilder();
        //TODO 文件开头
        data.append("$PBExportHeader$").append(JdbcUtil.FileName).append(".srd\n "+
                "release 12.5;\n" +
                "datawindow(units=0 timer_interval=0 color=1073741824 brushmode=0 transparency=0 gradient.angle=0 gradient.color=8421504 gradient.focus=0 gradient.repetition.count=0 gradient.repetition.length=100 gradient.repetition.mode=0 gradient.scale=100 gradient.spread=100 gradient.transparency=0 picture.blur=0 picture.clip.bottom=0 picture.clip.left=0 picture.clip.right=0 picture.clip.top=0 picture.mode=0 picture.scale.x=100 picture.scale.y=100 picture.transparency=0 processing=1 HTMLDW=no print.printername=\"\" print.documentname=\"\" print.orientation = 0 print.margin.left = 110 print.margin.right = 110 print.margin.top = 96 print.margin.bottom = 96 print.paper.source = 0 print.paper.size = 0 print.canusedefaultprinter=yes print.prompt=no print.buttons=no print.preview.buttons=no print.cliptext=no print.overrideprintjob=no print.collate=yes print.background=no print.preview.background=no print.preview.outline=yes hidegrayline=no showbackcoloronxp=no picture.file=\"\" grid.lines=0 )\n" +
                "header(height=92 color=\"536870912\" transparency=\"0\" gradient.color=\"8421504\" gradient.transparency=\"0\" gradient.angle=\"0\" brushmode=\"0\" gradient.repetition.mode=\"0\" gradient.repetition.count=\"0\" gradient.repetition.length=\"100\" gradient.focus=\"0\" gradient.scale=\"100\" gradient.spread=\"100\" )\n" +
                "summary(height=0 color=\"536870912\" transparency=\"0\" gradient.color=\"8421504\" gradient.transparency=\"0\" gradient.angle=\"0\" brushmode=\"0\" gradient.repetition.mode=\"0\" gradient.repetition.count=\"0\" gradient.repetition.length=\"100\" gradient.focus=\"0\" gradient.scale=\"100\" gradient.spread=\"100\" )\n" +
                "footer(height=0 color=\"536870912\" transparency=\"0\" gradient.color=\"8421504\" gradient.transparency=\"0\" gradient.angle=\"0\" brushmode=\"0\" gradient.repetition.mode=\"0\" gradient.repetition.count=\"0\" gradient.repetition.length=\"100\" gradient.focus=\"0\" gradient.scale=\"100\" gradient.spread=\"100\" )\n" +
                "detail(height=104 color=\"536870912\" transparency=\"0\" gradient.color=\"8421504\" gradient.transparency=\"0\" gradient.angle=\"0\" brushmode=\"0\" gradient.repetition.mode=\"0\" gradient.repetition.count=\"0\" gradient.repetition.length=\"100\" gradient.focus=\"0\" gradient.scale=\"100\" gradient.spread=\"100\" )\n");
        StringBuilder dataWindow = new StringBuilder();
        dataWindow.append("table(");
        for (String column : columns) {
            dataWindow.append(column).append("\n");
        }
        pBSql.deleteCharAt(pBSql.length() - 1);
        pBSql.append(" from ").append(JdbcUtil.TABLENAME).append("\"").append("\nupdate=\"").append(JdbcUtil.TABLENAME).append("\"").append("\nupdatewhere=1").append("\nupdatekeyinplace=no");
        dataWindow.append(pBSql).append(")\n");
        //System.out.println(dataWindow);
        //TODO SQL
        data.append(dataWindow);
        int index;
        String text_str, column_str;
        ArrayList<String> header = new ArrayList<>();
        ArrayList<String> detail = new ArrayList<>();
        int x = 255, id = 1, tabsequence = 10;
        for (index = 0; index < fieldName.size(); index++) {
            text_str = "text(band=header alignment=\"2\" " +
                    "text=\"title_" + index + "\" border=\"0\" color=\"33554432\" " +
                    "x=\"" + x + "\" y=\"8\" height=\"88\" width=\"600\" " +
                    "html.valueishtml=\"0\" name=" + fieldName.get(index) + "_t visible=\"1\" " +
                    "font.face=\"Tahoma\" font.height=\"-12\" " +
                    "font.weight=\"400\" font.family=\"2\" font.pitch=\"2\" " +
                    "font.charset=\"0\" background.mode=\"1\" " +
                    "background.color=\"536870912\" " +
                    "background.transparency=\"0\" " +
                    "background.gradient.color=\"8421504\" " +
                    "background.gradient.transparency=\"0\" " +
                    "background.gradient.angle=\"0\" background.brushmode=\"0\" " +
                    "background.gradient.repetition.mode=\"0\" " +
                    "background.gradient.repetition.count=\"0\" " +
                    "background.gradient.repetition.length=\"100\" " +
                    "background.gradient.focus=\"0\" background.gradient.scale=\"100\" " +
                    "background.gradient.spread=\"100\" tooltip.backcolor=\"134217752\" " +
                    "tooltip.delay.initial=\"0\" tooltip.delay.visible=\"32000\" " +
                    "tooltip.enabled=\"0\" tooltip.hasclosebutton=\"0\" tooltip.icon=\"0\" " +
                    "tooltip.isbubble=\"0\" tooltip.maxwidth=\"0\" tooltip.textcolor=\"134217751\" " +
                    "tooltip.transparency=\"0\" transparency=\"0\" )\n";
            header.add(text_str);
            column_str = "column(band=detail id=" + id + " alignment=\"2\" " +
                    "tabsequence=" + tabsequence + " border=\"0\" color=\"33554432\" " +
                    "x=\"" + x + "\" y=\"8\" height=\"88\" width=\"600\" " +
                    "format=\"[general]\" html.valueishtml=\"0\" " +
                    "name=" + fieldName.get(index) + " visible=\"1\" edit.limit="+precisions.get(index)+" edit.case=any " +
                    "edit.focusrectangle=no edit.autoselect=yes edit.autohscroll=yes " +
                    "font.face=\"Tahoma\" font.height=\"-12\" font.weight=\"400\" " +
                    "font.family=\"2\" font.pitch=\"2\" font.charset=\"0\" " +
                    "background.mode=\"0\" background.color=\"536870912~tIF(GetRow()=CurrentRow(),RGB(250,250,0),RGB(202,234,206))\" " +
                    "background.transparency=\"0\" background.gradient.color=\"8421504\" " +
                    "background.gradient.transparency=\"0\" background.gradient.angle=\"0\" " +
                    "background.brushmode=\"0\" background.gradient.repetition.mode=\"0\" " +
                    "background.gradient.repetition.count=\"0\" background.gradient.repetition.length=\"100\" " +
                    "background.gradient.focus=\"0\" background.gradient.scale=\"100\" background.gradient.spread=\"100\" " +
                    "tooltip.backcolor=\"134217752\" tooltip.delay.initial=\"0\" tooltip.delay.visible=\"32000\" " +
                    "tooltip.enabled=\"0\" tooltip.hasclosebutton=\"0\" tooltip.icon=\"0\" tooltip.isbubble=\"0\" " +
                    "tooltip.maxwidth=\"0\" tooltip.textcolor=\"134217751\" " +
                    "tooltip.transparency=\"0\" transparency=\"0\" )\n";
            detail.add(column_str);
            tabsequence = tabsequence + 10;
            x = x + 600;
            id++;
        }
        header.add("compute(band=header alignment=\"2\" expression=\"rowcount()\"border=\"0\" color=\"255\" x=\"5\" y=\"8\" height=\"88\" width=\"251\" format=\"[GENERAL]\" html.valueishtml=\"0\"  name=compute_2 visible=\"1\"  font.face=\"Tahoma\" font.height=\"-12\" font.weight=\"400\"  font.family=\"2\" font.pitch=\"2\" font.charset=\"0\" background.mode=\"1\" background.color=\"536870912\" background.transparency=\"0\" background.gradient.color=\"8421504\" background.gradient.transparency=\"0\" background.gradient.angle=\"0\" background.brushmode=\"0\" background.gradient.repetition.mode=\"0\" background.gradient.repetition.count=\"0\" background.gradient.repetition.length=\"100\" background.gradient.focus=\"0\" background.gradient.scale=\"100\" background.gradient.spread=\"100\" tooltip.backcolor=\"134217752\" tooltip.delay.initial=\"0\" tooltip.delay.visible=\"32000\" tooltip.enabled=\"0\" tooltip.hasclosebutton=\"0\" tooltip.icon=\"0\" tooltip.isbubble=\"0\" tooltip.maxwidth=\"0\" tooltip.textcolor=\"134217751\" tooltip.transparency=\"0\" transparency=\"0\" )\n" +
                "compute(band=detail alignment=\"2\" expression=\"getrow()\"border=\"0\" color=\"255\" x=\"5\" y=\"8\" height=\"88\" width=\"251\" format=\"[GENERAL]\" html.valueishtml=\"0\"  name=compute_1 visible=\"1\"  font.face=\"Tahoma\" font.height=\"-12\" font.weight=\"400\"  font.family=\"2\" font.pitch=\"2\" font.charset=\"0\" background.mode=\"0\" background.color=\"536870912~tIF(GetRow()=CurrentRow(),RGB(250,250,0),RGB(202,234,206)) \" background.transparency=\"0\" background.gradient.color=\"8421504\" background.gradient.transparency=\"0\" background.gradient.angle=\"0\" background.brushmode=\"0\" background.gradient.repetition.mode=\"0\" background.gradient.repetition.count=\"0\" background.gradient.repetition.length=\"100\" background.gradient.focus=\"0\" background.gradient.scale=\"100\" background.gradient.spread=\"100\" tooltip.backcolor=\"134217752\" tooltip.delay.initial=\"0\" tooltip.delay.visible=\"32000\" tooltip.enabled=\"0\" tooltip.hasclosebutton=\"0\" tooltip.icon=\"0\" tooltip.isbubble=\"0\" tooltip.maxwidth=\"0\" tooltip.textcolor=\"134217751\" tooltip.transparency=\"0\" transparency=\"0\" )\n");
        StringBuilder headerAndBetail = new StringBuilder();
        for (String text : header) {
            headerAndBetail.append(text);
        }
        for (String column : detail) {
            headerAndBetail.append(column);
        }
        /* TODO 表头与字段 */
        //System.out.println(headerAndBetail);
        data.append(headerAndBetail);
        /* TODO 文件结尾 */
        data.append("htmltable(border=\"1\" )\n" +
                "htmlgen(clientevents=\"1\" clientvalidation=\"1\" clientcomputedfields=\"1\" clientformatting=\"0\" clientscriptable=\"0\" generatejavascript=\"1\" encodeselflinkargs=\"1\" netscapelayers=\"0\" pagingmethod=0 generatedddwframes=\"1\" )\n" +
                "xhtmlgen() cssgen(sessionspecific=\"0\" )\n" +
                "xmlgen(inline=\"0\" )\n" +
                "xsltgen()\n" +
                "jsgen()\n" +
                "export.xml(headgroups=\"1\" includewhitespace=\"0\" metadatatype=0 savemetadata=0 )\n" +
                "import.xml()\n" +
                "export.pdf(method=0 distill.custompostscript=\"0\" xslfop.print=\"0\" )\n" +
                "export.xhtml()");
        /* 文件生成 */
        fileGenerate(data);
    }
    /**
     * FreeFrom风格的数据窗口生成
     */
    public static void DataWindowFreeFrom(Connection conn){
        /* 拼凑SQL */
        String sql = JdbcUtil.SQL + JdbcUtil.TABLENAME;
        /* 获取主键 */
        ArrayList<String> keys = DbKeySet.keysSet(conn);
        /* 字段列名集合 */
        ArrayList<String> fieldName = new ArrayList<>();
        /* 表头字段集合 */
        ArrayList<String> columns = new ArrayList<>();

        PreparedStatement stmt;
        StringBuilder pBSql = new StringBuilder("retrieve=\"select ");
        try {
            stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery(sql);
            ResultSetMetaData data = rs.getMetaData();
            for (int i = 1; i <= data.getColumnCount(); i++) {
                /* 或者字段名 */
                String columnName = data.getColumnName(i);
                /* 获取字段名的类型 */
                String columnTypeName = data.getColumnTypeName(i);
                /* 获取字段的长度 */
                int precision = data.getPrecision(i);

                // 生成DataWindow
                String dbName = JdbcUtil.TABLENAME + "." + columnName;
                fieldName.add(columnName.toLowerCase());
                pBSql.append(columnName).append(",");
                if ("VARCHAR2".equals(columnTypeName)) {
                    columnTypeName = "CHAR";
                }
                boolean b = false;
                for (String key : keys) {
                    if (columnName.equals(key)) {
                        b = true;
                        break;
                    }
                }
                if (b) {
                    if ("NUMBER".equals(columnTypeName)) {
                        columns.add("column=(type=" + columnTypeName + " update=yes updatewhereclause=yes key=yes name=" + columnName + " dbname=\"" + dbName + "\")");
                        continue;
                    }
                    if ("INTEGER".equals(columnTypeName)) {
                        columns.add("column=(type=decimal(0) update=yes updatewhereclause=yes key=yes name=" + columnName + " dbname=\"" + dbName + "\")");
                        continue;
                    }
                    columns.add("column=(type=" + columnTypeName + "(" + precision + ") update=yes updatewhereclause=yes key=yes name=" + columnName + " dbname=\"" + dbName + "\")");
                    continue;
                }

                if ("NUMBER".equals(columnTypeName)) {
                    columns.add("column=(type=" + columnTypeName + " update=yes updatewhereclause=yes name=" + columnName + " dbname=\"" + dbName + "\")");
                    continue;
                }
                if ("INTEGER".equals(columnTypeName)) {
                    columns.add("column=(type=decimal(0) update=yes updatewhereclause=yes name=" + columnName + " dbname=\"" + dbName + "\")");
                    continue;
                }
                columns.add("column=(type=" + columnTypeName + "(" + precision + ") update=yes updatewhereclause=yes name=" + columnName + " dbname=\"" + dbName + "\")");
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            JdbcUtil.closeConnection(conn);
        }
        StringBuilder data = new StringBuilder();
        //TODO 文件开头
        data.append("$PBExportHeader$)").append(JdbcUtil.FileName).append(".srd\n "+
                "release 12.5;\n" +
                "datawindow(units=0 timer_interval=0 color=1073741824 brushmode=0 transparency=0 gradient.angle=0 gradient.color=8421504 gradient.focus=0 gradient.repetition.count=0 gradient.repetition.length=100 gradient.repetition.mode=0 gradient.scale=100 gradient.spread=100 gradient.transparency=0 picture.blur=0 picture.clip.bottom=0 picture.clip.left=0 picture.clip.right=0 picture.clip.top=0 picture.mode=0 picture.scale.x=100 picture.scale.y=100 picture.transparency=0 processing=1 HTMLDW=no print.printername=\"\" print.documentname=\"\" print.orientation = 0 print.margin.left = 110 print.margin.right = 110 print.margin.top = 96 print.margin.bottom = 96 print.paper.source = 0 print.paper.size = 0 print.canusedefaultprinter=yes print.prompt=no print.buttons=no print.preview.buttons=no print.cliptext=no print.overrideprintjob=no print.collate=yes print.background=no print.preview.background=no print.preview.outline=yes hidegrayline=no showbackcoloronxp=no picture.file=\"\" grid.lines=0 )\n" +
                "header(height=92 color=\"536870912\" transparency=\"0\" gradient.color=\"8421504\" gradient.transparency=\"0\" gradient.angle=\"0\" brushmode=\"0\" gradient.repetition.mode=\"0\" gradient.repetition.count=\"0\" gradient.repetition.length=\"100\" gradient.focus=\"0\" gradient.scale=\"100\" gradient.spread=\"100\" )\n" +
                "summary(height=0 color=\"536870912\" transparency=\"0\" gradient.color=\"8421504\" gradient.transparency=\"0\" gradient.angle=\"0\" brushmode=\"0\" gradient.repetition.mode=\"0\" gradient.repetition.count=\"0\" gradient.repetition.length=\"100\" gradient.focus=\"0\" gradient.scale=\"100\" gradient.spread=\"100\" )\n" +
                "footer(height=0 color=\"536870912\" transparency=\"0\" gradient.color=\"8421504\" gradient.transparency=\"0\" gradient.angle=\"0\" brushmode=\"0\" gradient.repetition.mode=\"0\" gradient.repetition.count=\"0\" gradient.repetition.length=\"100\" gradient.focus=\"0\" gradient.scale=\"100\" gradient.spread=\"100\" )\n" +
                "detail(height=104 color=\"536870912\" transparency=\"0\" gradient.color=\"8421504\" gradient.transparency=\"0\" gradient.angle=\"0\" brushmode=\"0\" gradient.repetition.mode=\"0\" gradient.repetition.count=\"0\" gradient.repetition.length=\"100\" gradient.focus=\"0\" gradient.scale=\"100\" gradient.spread=\"100\" )\n");
        StringBuilder dataWindow = new StringBuilder();
        dataWindow.append("table(");
        for (String column : columns) {
            dataWindow.append(column).append("\n");
        }
        pBSql.deleteCharAt(pBSql.length() - 1);
        pBSql.append(" from ").append(JdbcUtil.TABLENAME).append("\"").append("\nupdate=\"").append(JdbcUtil.TABLENAME).append("\"").append("\nupdatewhere=1").append("\nupdatekeyinplace=no");
        dataWindow.append(pBSql).append(")\n");
        //System.out.println(dataWindow);
        //TODO SQL
        data.append(dataWindow);
        int index;
        String text_str, column_str;
        ArrayList<String> header = new ArrayList<>();
        ArrayList<String> detail = new ArrayList<>();
        int x = 255, id = 1, tabsequence = 10;
        for (index = 0; index < fieldName.size(); index++) {
            text_str = "text(band=header alignment=\"2\" " +
                    "text=\"title_" + index + "\" border=\"0\" color=\"33554432\" " +
                    "x=\"" + x + "\" y=\"8\" height=\"88\" width=\"600\" " +
                    "html.valueishtml=\"0\" name=" + fieldName.get(index) + "_t visible=\"1\" " +
                    "font.face=\"Tahoma\" font.height=\"-12\" " +
                    "font.weight=\"400\" font.family=\"2\" font.pitch=\"2\" " +
                    "font.charset=\"0\" background.mode=\"1\" " +
                    "background.color=\"536870912\" " +
                    "background.transparency=\"0\" " +
                    "background.gradient.color=\"8421504\" " +
                    "background.gradient.transparency=\"0\" " +
                    "background.gradient.angle=\"0\" background.brushmode=\"0\" " +
                    "background.gradient.repetition.mode=\"0\" " +
                    "background.gradient.repetition.count=\"0\" " +
                    "background.gradient.repetition.length=\"100\" " +
                    "background.gradient.focus=\"0\" background.gradient.scale=\"100\" " +
                    "background.gradient.spread=\"100\" tooltip.backcolor=\"134217752\" " +
                    "tooltip.delay.initial=\"0\" tooltip.delay.visible=\"32000\" " +
                    "tooltip.enabled=\"0\" tooltip.hasclosebutton=\"0\" tooltip.icon=\"0\" " +
                    "tooltip.isbubble=\"0\" tooltip.maxwidth=\"0\" tooltip.textcolor=\"134217751\" " +
                    "tooltip.transparency=\"0\" transparency=\"0\" )\n";
            header.add(text_str);
            column_str = "column(band=detail id=" + id + " alignment=\"2\" " +
                    "tabsequence=" + tabsequence + " border=\"0\" color=\"33554432\" " +
                    "x=\"" + x + "\" y=\"8\" height=\"88\" width=\"600\" " +
                    "format=\"[general]\" html.valueishtml=\"0\" " +
                    "name=" + fieldName.get(index) + " visible=\"1\" edit.limit=20 edit.case=any " +
                    "edit.focusrectangle=no edit.autoselect=yes edit.autohscroll=yes " +
                    "font.face=\"Tahoma\" font.height=\"-12\" font.weight=\"400\" " +
                    "font.family=\"2\" font.pitch=\"2\" font.charset=\"0\" " +
                    "background.mode=\"0\" background.color=\"536870912~tIF(GetRow()=CurrentRow(),RGB(250,250,0),RGB(202,234,206))\" " +
                    "background.transparency=\"0\" background.gradient.color=\"8421504\" " +
                    "background.gradient.transparency=\"0\" background.gradient.angle=\"0\" " +
                    "background.brushmode=\"0\" background.gradient.repetition.mode=\"0\" " +
                    "background.gradient.repetition.count=\"0\" background.gradient.repetition.length=\"100\" " +
                    "background.gradient.focus=\"0\" background.gradient.scale=\"100\" background.gradient.spread=\"100\" " +
                    "tooltip.backcolor=\"134217752\" tooltip.delay.initial=\"0\" tooltip.delay.visible=\"32000\" " +
                    "tooltip.enabled=\"0\" tooltip.hasclosebutton=\"0\" tooltip.icon=\"0\" tooltip.isbubble=\"0\" " +
                    "tooltip.maxwidth=\"0\" tooltip.textcolor=\"134217751\" " +
                    "tooltip.transparency=\"0\" transparency=\"0\" )\n";
            detail.add(column_str);
            tabsequence = tabsequence + 10;
            x = x + 600;
            id++;
        }
        header.add("compute(band=header alignment=\"2\" expression=\"rowcount()\"border=\"0\" color=\"255\" x=\"5\" y=\"8\" height=\"88\" width=\"251\" format=\"[GENERAL]\" html.valueishtml=\"0\"  name=compute_2 visible=\"1\"  font.face=\"Tahoma\" font.height=\"-12\" font.weight=\"400\"  font.family=\"2\" font.pitch=\"2\" font.charset=\"0\" background.mode=\"1\" background.color=\"536870912\" background.transparency=\"0\" background.gradient.color=\"8421504\" background.gradient.transparency=\"0\" background.gradient.angle=\"0\" background.brushmode=\"0\" background.gradient.repetition.mode=\"0\" background.gradient.repetition.count=\"0\" background.gradient.repetition.length=\"100\" background.gradient.focus=\"0\" background.gradient.scale=\"100\" background.gradient.spread=\"100\" tooltip.backcolor=\"134217752\" tooltip.delay.initial=\"0\" tooltip.delay.visible=\"32000\" tooltip.enabled=\"0\" tooltip.hasclosebutton=\"0\" tooltip.icon=\"0\" tooltip.isbubble=\"0\" tooltip.maxwidth=\"0\" tooltip.textcolor=\"134217751\" tooltip.transparency=\"0\" transparency=\"0\" )\n" +
                "compute(band=detail alignment=\"2\" expression=\"getrow()\"border=\"0\" color=\"255\" x=\"5\" y=\"8\" height=\"88\" width=\"251\" format=\"[GENERAL]\" html.valueishtml=\"0\"  name=compute_1 visible=\"1\"  font.face=\"Tahoma\" font.height=\"-12\" font.weight=\"400\"  font.family=\"2\" font.pitch=\"2\" font.charset=\"0\" background.mode=\"0\" background.color=\"536870912~tIF(GetRow()=CurrentRow(),RGB(250,250,0),RGB(202,234,206)) \" background.transparency=\"0\" background.gradient.color=\"8421504\" background.gradient.transparency=\"0\" background.gradient.angle=\"0\" background.brushmode=\"0\" background.gradient.repetition.mode=\"0\" background.gradient.repetition.count=\"0\" background.gradient.repetition.length=\"100\" background.gradient.focus=\"0\" background.gradient.scale=\"100\" background.gradient.spread=\"100\" tooltip.backcolor=\"134217752\" tooltip.delay.initial=\"0\" tooltip.delay.visible=\"32000\" tooltip.enabled=\"0\" tooltip.hasclosebutton=\"0\" tooltip.icon=\"0\" tooltip.isbubble=\"0\" tooltip.maxwidth=\"0\" tooltip.textcolor=\"134217751\" tooltip.transparency=\"0\" transparency=\"0\" )\n");
        StringBuilder headerAndBetail = new StringBuilder();
        for (String text : header) {
            headerAndBetail.append(text);
        }
        for (String column : detail) {
            headerAndBetail.append(column);
        }
        /* TODO 表头与字段 */
        //System.out.println(headerAndBetail);
        data.append(headerAndBetail);
        /* TODO 文件结尾 */
        data.append("htmltable(border=\"1\" )\n" +
                "htmlgen(clientevents=\"1\" clientvalidation=\"1\" clientcomputedfields=\"1\" clientformatting=\"0\" clientscriptable=\"0\" generatejavascript=\"1\" encodeselflinkargs=\"1\" netscapelayers=\"0\" pagingmethod=0 generatedddwframes=\"1\" )\n" +
                "xhtmlgen() cssgen(sessionspecific=\"0\" )\n" +
                "xmlgen(inline=\"0\" )\n" +
                "xsltgen()\n" +
                "jsgen()\n" +
                "export.xml(headgroups=\"1\" includewhitespace=\"0\" metadatatype=0 savemetadata=0 )\n" +
                "import.xml()\n" +
                "export.pdf(method=0 distill.custompostscript=\"0\" xslfop.print=\"0\" )\n" +
                "export.xhtml()");
        /* 文件生成 */
        fileGenerate(data);
    }
    /**
     * Tabular风格的数据窗口生成
     */
    public static void DataWindowTabular(Connection conn){}

    /**
     * 文件生成
     */
    private static void fileGenerate(StringBuilder data){
        /* 文件生成 */
        File file = new File(JdbcUtil.FileName+".srd");
        if (!file.exists()) {
            try {
                boolean newFile = file.createNewFile();
                if (newFile) {
                    System.out.println("文件创建成功");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Writer write;
        try {
            write = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8);
            write.write(String.valueOf(data));
            write.flush();
            write.close();
            System.out.println("complete!!!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
