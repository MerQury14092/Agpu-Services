package com.merqury.agpu.timetable.service;


import com.merqury.agpu.timetable.DTO.Day;
import com.merqury.agpu.timetable.DTO.Discipline;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
@Log4j2
public class ImageService {
    private Font font;

    public ImageService() throws IOException, FontFormatException {
        this.font = Font.createFont(Font.TRUETYPE_FONT, ImageService.class.getResourceAsStream("/fonts/jetbrains-mono(bold).ttf")).deriveFont(Font.BOLD, 16f);
    }

    public BufferedImage getImageByTimetableOf6DaysHorizontal(Day[] days, int cellWidth, boolean full){
        int max_pairs = countPairs(days[0]);
        for(Day day: days)
            if(countPairs(day) > max_pairs)
                max_pairs = countPairs(day);
        BufferedImage res = new BufferedImage(cellWidth*(full?7:max_pairs)+150, 200*6+150, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = res.createGraphics();
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, res.getWidth(), res.getHeight());
        g.setColor(Color.BLACK);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int y = -50;



        g.drawImage(headerForHorizontal(cellWidth), 0, 0, null);

        for(Day day: days) {
            if(full)
                g.drawImage(getImageByTimetableOfDayHorizontal(day, cellWidth, true), 0, y += 200, null);
            else
                g.drawImage(getImageByTimetableOfDayHorizontal(day, cellWidth, true, max_pairs), 0, y+=200, null);
        }

        return res;
    }

    /**
     * @param cellWidth - cell width
     * @return BufferedImage with size - {7*cellWidth+150; 150}
     */
    private BufferedImage headerForHorizontal(int cellWidth){
        BufferedImage header = new BufferedImage(cellWidth*7+150, 150, BufferedImage.TYPE_INT_RGB);

        int x = 150-cellWidth;

        Graphics2D header_g = header.createGraphics();
        header_g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        header_g.setColor(Color.WHITE);
        header_g.fillRect(0, 0, header.getWidth(), header.getHeight());
        header_g.setColor(Color.BLACK);

        header_g.setStroke(new BasicStroke(3));
        header_g.setColor(Color.decode("#d0d0d0"));
        header_g.fillRect(0, 0, 150, header.getHeight());
        header_g.setColor(Color.BLACK);
        header_g.drawRect(0, 0, 150, header.getHeight());
        for (int i = 0; i < 7; i++) {
            int offset = 25;
            header_g.setColor(Color.decode("#d0d0d0"));
            header_g.fillRect(x+=cellWidth, 0, cellWidth, header.getHeight());
            header_g.setColor(Color.BLACK);
            header_g.drawRect(x, 0, cellWidth, header.getHeight());
            header_g.drawRect(x, header.getHeight()/2+offset, cellWidth, header.getHeight()/2-offset);
            header_g.drawRect(x, header.getHeight()/2+offset, cellWidth/2, header.getHeight()/2-offset);
        }

        int x_buf = 150-cellWidth;

        printString_new(header_g, "I пара", new Rectangle(x_buf+=cellWidth, -header_g.getFontMetrics().getAscent()-10, cellWidth, 100), font.deriveFont(24f));
        printString_new(header_g, "8:00-9:40", new Rectangle(x_buf, header_g.getFontMetrics().getAscent(), cellWidth, 100), font.deriveFont(24f));
        printString_new(header_g, "II пара", new Rectangle(x_buf+=cellWidth, -header_g.getFontMetrics().getAscent()-10, cellWidth, 100), font.deriveFont(24f));
        printString_new(header_g, "9:40-11:10", new Rectangle(x_buf, header_g.getFontMetrics().getAscent(), cellWidth, 100), font.deriveFont(24f));
        printString_new(header_g, "III пара", new Rectangle(x_buf+=cellWidth, -header_g.getFontMetrics().getAscent()-10, cellWidth, 100), font.deriveFont(24f));
        printString_new(header_g, "11:40-13:10", new Rectangle(x_buf, header_g.getFontMetrics().getAscent(), cellWidth, 100), font.deriveFont(24f));
        printString_new(header_g, "IV пара", new Rectangle(x_buf+=cellWidth, -header_g.getFontMetrics().getAscent()-10, cellWidth, 100), font.deriveFont(24f));
        printString_new(header_g, "13:30-15:00", new Rectangle(x_buf, header_g.getFontMetrics().getAscent(), cellWidth, 100), font.deriveFont(24f));
        printString_new(header_g, "V пара", new Rectangle(x_buf+=cellWidth, -header_g.getFontMetrics().getAscent()-10, cellWidth, 100), font.deriveFont(24f));
        printString_new(header_g, "15:10-16:40", new Rectangle(x_buf, header_g.getFontMetrics().getAscent(), cellWidth, 100), font.deriveFont(24f));
        printString_new(header_g, "VI пара", new Rectangle(x_buf+=cellWidth, -header_g.getFontMetrics().getAscent()-10, cellWidth, 100), font.deriveFont(24f));
        printString_new(header_g, "16:50-18:20", new Rectangle(x_buf, header_g.getFontMetrics().getAscent(), cellWidth, 100), font.deriveFont(24f));
        printString_new(header_g, "VII пара", new Rectangle(x_buf+=cellWidth, -header_g.getFontMetrics().getAscent()-10, cellWidth, 100), font.deriveFont(24f));
        printString_new(header_g, "18:30-20:00", new Rectangle(x_buf, header_g.getFontMetrics().getAscent(), cellWidth, 100), font.deriveFont(24f));

        for (int i = 0; i < 14; i++) {
            int offset = 25;
            if(i % 2 == 0)
                printString(header_g, "I", 300+i*cellWidth, header.getHeight()/2+offset*7, cellWidth/2, header.getHeight()/2-offset, font.deriveFont(20f));
            if(i % 2 == 1)
                printString(header_g, "II", 300+i*cellWidth, header.getHeight()/2+offset*7, cellWidth/2, header.getHeight()/2-offset, font.deriveFont(20f));
        }
        return header;
    }

    public BufferedImage headerForVertical(){
        BufferedImage header = new BufferedImage(150, 1550, BufferedImage.TYPE_INT_RGB);

        Graphics2D header_g = header.createGraphics();
        header_g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        header_g.setColor(Color.WHITE);
        header_g.fillRect(0, 0, header.getWidth(), header.getHeight());
        header_g.setColor(Color.BLACK);

        header_g.setStroke(new BasicStroke(3));
        header_g.setColor(Color.decode("#d0d0d0"));
        header_g.fillRect(0, 0, header.getWidth(), 150);
        header_g.setColor(Color.BLACK);
        header_g.drawRect(0, 0, 150, 150);
        for (int i = 0; i < 7; i++) {
            int offset = 25;
            header_g.drawRect(0, 150+i*200, 150, 200);
        }


        int y_buf = -50;

        int offset = 20;
        float fontSize = 24;

        printString_new(header_g, "I пара", new Rectangle(0, -offset+(y_buf+=200), 150, 150), font.deriveFont(fontSize));
        printString_new(header_g, "8:00", new Rectangle(0, y_buf+offset, 150, 150), font.deriveFont(fontSize));
        printString_new(header_g, "9:40", new Rectangle(0, (int) (y_buf+offset*2.5), 150, 150), font.deriveFont(fontSize));

        printString_new(header_g, "II пара", new Rectangle(0, -offset+(y_buf+=200), 150, 150), font.deriveFont(fontSize));
        printString_new(header_g, "9:40", new Rectangle(0, y_buf+offset, 150, 150), font.deriveFont(fontSize));
        printString_new(header_g, "11:10", new Rectangle(0, (int) (y_buf+offset*2.5), 150, 150), font.deriveFont(fontSize));


        printString_new(header_g, "III пара", new Rectangle(0, -offset+(y_buf+=200), 150, 150), font.deriveFont(fontSize));
        printString_new(header_g, "11:40", new Rectangle(0, y_buf+offset, 150, 150), font.deriveFont(fontSize));
        printString_new(header_g, "13:10", new Rectangle(0, (int) (y_buf+offset*2.5), 150, 150), font.deriveFont(fontSize));


        printString_new(header_g, "IV пара", new Rectangle(0, -offset+(y_buf+=200), 150, 150), font.deriveFont(fontSize));
        printString_new(header_g, "13:30", new Rectangle(0, y_buf+offset, 150, 150), font.deriveFont(fontSize));
        printString_new(header_g, "15:00", new Rectangle(0, (int) (y_buf+offset*2.5), 150, 150), font.deriveFont(fontSize));


        printString_new(header_g, "V пара", new Rectangle(0, -offset+(y_buf+=200), 150, 150), font.deriveFont(fontSize));
        printString_new(header_g, "15:10", new Rectangle(0, y_buf+offset, 150, 150), font.deriveFont(fontSize));
        printString_new(header_g, "16:40", new Rectangle(0, (int) (y_buf+offset*2.5), 150, 150), font.deriveFont(fontSize));


        printString_new(header_g, "VI пара", new Rectangle(0, -offset+(y_buf+=200), 150, 150), font.deriveFont(fontSize));
        printString_new(header_g, "16:50", new Rectangle(0, y_buf+offset, 150, 150), font.deriveFont(fontSize));
        printString_new(header_g, "18:20", new Rectangle(0, (int) (y_buf+offset*2.5), 150, 150), font.deriveFont(fontSize));


        printString_new(header_g, "VII пара", new Rectangle(0, -offset+(y_buf+=200), 150, 150), font.deriveFont(fontSize));
        printString_new(header_g, "18:30", new Rectangle(0, y_buf+offset, 150, 150), font.deriveFont(fontSize));
        printString_new(header_g, "20:00", new Rectangle(0, (int) (y_buf+offset*2.5), 150, 150), font.deriveFont(fontSize));

        return header;
    }

    private void printString_new(Graphics2D g, String str, Rectangle r, Font font){
        Font last = g.getFont();
        g.setFont(font);
        FontMetrics metrics = g.getFontMetrics();

        int w = metrics.stringWidth(str);
        int h = metrics.getHeight();

        g.drawString(str, r.x+(r.width-w)/2, r.y+(r.height-h)/2+metrics.getAscent());

        g.setFont(last);
    }

    public BufferedImage getImageByTimetableOfDayHorizontal(Day day, int cellWidth, boolean forTable){
        return getImageByTimetableOfDayHorizontal(day, cellWidth, forTable, 7);
    }
    public BufferedImage getImageByTimetableOfDayVertical(Day day, int cellWidth, boolean forTable){
        return getImageByTimetableOfDayVertical(day, cellWidth, forTable, 7);
    }



    public BufferedImage getImageByTimetableOfDayHorizontal(Day day, int cellWidth, boolean forTable, int countCells){
        for(Discipline disc: day.getDisciplines()){
            disc.setColspan(switch (disc.getTime()){
                case "8:00-9:30" -> 0;
                case "9:40-11:10" -> 1;
                case "11:40-13:10" -> 2;
                case "13:30-15:00" -> 3;
                case "15:10-16:40" -> 4;
                case "16:50-18:20" -> 5;
                case "18:30-20:00" -> 6;
                default -> -1;
            });
        }
        BufferedImage res = new BufferedImage(cellWidth*(forTable?countCells:countPairs(day))+150, 350-(forTable?150:0), BufferedImage.TYPE_INT_RGB);
        Graphics2D g = res.createGraphics();
        g.setColor(Color.WHITE);
        g.fillRect(0, 150-(forTable?150:0), res.getWidth(), res.getHeight());
        g.setColor(Color.BLACK);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g.setStroke(new BasicStroke(3));

        g.drawRect(0, 150-(forTable?150:0), 150, res.getHeight());

        if(!forTable)
            g.drawImage(headerForHorizontal(cellWidth), 0, 0, null);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");


        LocalDate date = LocalDate.parse(day.getDate(), formatter);

        String dayOfWeek = date.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.forLanguageTag("RU"));
        dayOfWeek = Character.toUpperCase(dayOfWeek.charAt(0))+dayOfWeek.substring(1);


        printString_new(g, dayOfWeek, new Rectangle(0, 200-(forTable?150:0), 150, 20), font.deriveFont(20f));
        printString_new(g, day.getDate(), new Rectangle(0, 240-(forTable?150:0), 150, 20), font.deriveFont(20f));

        int i = 0;

        List<Discipline> disciplines = day.getDisciplines();
        for (int j = 0; j < disciplines.size(); j++) {
            Discipline disc = disciplines.get(j);
            if(j != disciplines.size() - 1){
                if(disc.getColspan() == disciplines.get(j+1).getColspan())
                    continue;
            }
            if(j != 0 && disc.getColspan() == disciplines.get(j-1).getColspan()){
                g.drawImage(getImageByTimetableOfSubDiscipline(disciplines.get(j-1), disc, cellWidth), 150 + i * cellWidth, 150-(forTable?150:0), null);
                i++;
                continue;
            }
            g.drawImage(getImageByTimetableOfDiscipline(disc, cellWidth), 150 + disc.getColspan() * cellWidth, 150-(forTable?150:0), null);
            i++;
        }

        for (int j = 0; j < (forTable?countCells:countPairs(day)); j++) {
            g.drawRect(150+j*cellWidth, 150-(forTable?150:0), cellWidth, res.getHeight());
        }

        g.setStroke(new BasicStroke(3));
        g.drawRect(0, 150-(forTable?150:0), res.getWidth(), res.getHeight());

        return res;
    }

    public BufferedImage getImageByTimetableOfDayVertical(Day day, int cellWidth, boolean forTable, int countCells){
        for(Discipline disc: day.getDisciplines()){
            disc.setColspan(switch (disc.getTime()){
                case "8:00-9:30" -> 0;
                case "9:40-11:10" -> 1;
                case "11:40-13:10" -> 2;
                case "13:30-15:00" -> 3;
                case "15:10-16:40" -> 4;
                case "16:50-18:20" -> 5;
                case "18:30-20:00" -> 6;
                default -> -1;
            });
        }
        BufferedImage res = new BufferedImage((forTable?0:150)+cellWidth, 150+200*(forTable ? countCells : countPairs(day)), BufferedImage.TYPE_INT_RGB);
        Graphics2D g = res.createGraphics();
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, res.getWidth(), res.getHeight());
        g.setColor(Color.BLACK);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g.setStroke(new BasicStroke(3));

        g.setColor(Color.decode("#d0d0d0"));
        g.fillRect((forTable?0:150), 0, cellWidth, 150);
        g.setColor(Color.BLACK);
        g.drawRect((forTable?0:150), 0, cellWidth, res.getHeight());

        if(!forTable)
            g.drawImage(headerForVertical(), 0, 0, null);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");


        LocalDate date = LocalDate.parse(day.getDate(), formatter);

        String dayOfWeek = date.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.forLanguageTag("RU"));
        dayOfWeek = Character.toUpperCase(dayOfWeek.charAt(0))+dayOfWeek.substring(1);


        printString_new(g, dayOfWeek, new Rectangle(forTable?0:150, 25, cellWidth, 20), font.deriveFont(20f));
        printString_new(g, day.getDate(), new Rectangle(forTable?0:150, 50, cellWidth, 20), font.deriveFont(20f));

        g.drawRect(forTable?0:150, 100, cellWidth, 50);
        g.drawRect(forTable?0:150, 100, cellWidth/2, 50);

        printString_new(g, "I", new Rectangle(forTable?0:150, 100, cellWidth/2, 50), font.deriveFont(24f));
        printString_new(g, "II", new Rectangle((forTable?0:150)+cellWidth/2, 100, cellWidth/2, 50), font.deriveFont(24f));

        int i = 0;

        List<Discipline> disciplines = day.getDisciplines();
        for (int j = 0; j < disciplines.size(); j++) {
            Discipline disc = disciplines.get(j);
            if(j != disciplines.size() - 1){
                if(disc.getColspan() == disciplines.get(j+1).getColspan())
                    continue;
            }
            if(j != 0 && disc.getColspan() == disciplines.get(j-1).getColspan()){
                g.drawImage(getImageByTimetableOfSubDiscipline(disciplines.get(j-1), disc, cellWidth), 150-(forTable?150:0), 150+i*200, null);
                i++;
                continue;
            }
            g.drawImage(getImageByTimetableOfDiscipline(disc, cellWidth), 150-(forTable?150:0), 150+200*disc.getColspan(), null);
            i++;
        }

        for (int j = 0; j < countPairs(day); j++) {
            g.drawRect(150-(forTable?150:0), 150+j*200, cellWidth, 200);
        }

        g.setStroke(new BasicStroke(3));
        g.drawRect(0, 150-(forTable?150:0), res.getWidth(), res.getHeight());

        if(forTable)
            for (int j = 0; j < countCells; j++) {
                g.drawRect(0, 150+200*j, cellWidth, 200);
            }

        return res;
    }

    private int countPairs(Day day){
        return switch (day.getDisciplines().get(day.getDisciplines().size()-1).getTime()){
            case "8:00-9:30" -> 1;
            case "9:40-11:10" -> 2;
            case "11:40-13:10" -> 3;
            case "13:30-15:00" -> 4;
            case "15:10-16:40" -> 5;
            case "16:50-18:20" -> 6;
            case "18:30-20:00" -> 7;
            default -> 0;
        };
    }

    public BufferedImage getImageByTimetableOfDiscipline(Discipline disc, int width){
        BufferedImage res = new BufferedImage(width, 200, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = res.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(
                switch (disc.getType()){
                    case lec -> Color.decode("#f7e3e7");
                    case prac -> Color.decode("#e8e582");
                    case lab -> Color.decode("#d3e2d0");
                    case exam -> Color.decode("#f6574c");
                    case fepo -> Color.decode("#ea48d0");
                    default -> Color.WHITE;
                }
        );
        g.fillRect(0, 0, res.getWidth(), res.getHeight());
        g.setColor(Color.BLACK);

        int y = 20;
        g.setFont(font);
        int fontHeight = g.getFontMetrics().getHeight();
        for(String line: lines(g, disc.getName(), res.getWidth(), font))
            printString(g, line, 0, y+= (int) (fontHeight*1.5), res.getWidth(), fontHeight, font);
        y = 230;
        printString(g, disc.getTeacherName(), 0, y+= (int) (fontHeight*1.5), res.getWidth(), fontHeight, font);
        printString(g, disc.getGroupName(), 0, y+= (int) (fontHeight*1.5), res.getWidth(), fontHeight, font);
        printString(g, "Аудитория: "+disc.getAudienceId(), 0, y+= (int) (fontHeight*1.5), res.getWidth(), fontHeight, font);
        printString(g,
                switch (disc.getType()){
                    case lab -> "Лаба";
                    case fepo -> "ФЭПО";
                    case cred -> "Зачёт";
                    case cons -> "Консультация";
                    case prac -> "Практика";
                    case hol -> "Праздник";
                    case lec -> "Лекция";
                    case exam -> "Экзамен";
                    case none -> "";
                }
                , 0, y+= (int) (fontHeight*1.5), res.getWidth(), fontHeight, font);
        return res;
    }

    private void printString(Graphics2D g, String str, int x, int y, int w, int h, Font font){
        Font last = g.getFont();
        g.setFont(font);
        FontMetrics metrics = g.getFontMetrics();
        int width = metrics.stringWidth(str);
        int height = metrics.getHeight();
        g.drawString(str, (x+w-width)/2, (y+h-height)/2);
        g.setFont(last);
    }

    private List<String> lines(Graphics2D g, String str, int width, Font font){
        Font last = g.getFont();
        g.setFont(font);
        FontMetrics metrics = g.getFontMetrics();
        List<String> res = new ArrayList<>();
        List<String> words = List.of(str.split(" "));
        for(int i = 0; !words.isEmpty(); i++){
            if(i < words.size() && metrics.stringWidth(apply(words.subList(0, i))) > width){
                --i;
                res.add(apply(words.subList(0, i)));
                words = words.subList(i, words.size());
                i = 0;
            }
            else if(i >= words.size()){
                if(metrics.stringWidth(apply(words)) > width){
                    res.add(apply(words.subList(0, words.size()-1)));
                    res.add(words.get(words.size()-1));
                }
                else
                    res.add(apply(words));
                words = new ArrayList<>();
            }
        }
        g.setFont(last);
        return res;
    }

    private String apply(List<String> arr){
        StringBuilder res = new StringBuilder();
        for(String str: arr)
            res.append(str).append(" ");
        return res.toString().trim();
    }

    public BufferedImage getImageByTimetableOfSubDiscipline(Discipline disc1, Discipline disc2, int width){
        BufferedImage res = new BufferedImage(width, 200, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = res.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g.drawImage(getImageByTimetableOfDiscipline(disc1, width/2), 0, 0, null);
        g.drawImage(getImageByTimetableOfDiscipline(disc2, width/2), width/2, 0, null);
        g.setStroke(new BasicStroke(3));
        g.setColor(Color.BLACK);
        g.drawLine(width/2, 0, width/2, res.getHeight());

        return res;
    }
}
