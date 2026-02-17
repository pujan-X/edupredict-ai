package com.edupredict.service;

import com.edupredict.model.Student;
import com.edupredict.repository.StudentRepository;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.List;

@Service
public class ReportService {

    @Autowired
    private StudentRepository repository;

    public ByteArrayInputStream generateStudentReport() {
        Document document = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            PdfWriter.getInstance(document, out);
            document.open();

            // Add Title
            Font fontHeader = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20);
            Paragraph title = new Paragraph("EduPredict - Student Performance Report", fontHeader);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);
            document.add(Chunk.NEWLINE);

            // Create Table
            PdfPTable table = new PdfPTable(5); // 5 columns
            table.setWidthPercentage(100);
            
            // Add Headers
            String[] headers = {"ID", "Name", "GPA", "Attendance", "Risk Level"};
            for (String header : headers) {
                PdfPCell cell = new PdfPCell(new Phrase(header));
                cell.setBackgroundColor(java.awt.Color.LIGHT_GRAY);
                cell.setPadding(5);
                table.addCell(cell);
            }

            // Add Data
            List<Student> students = repository.findAll();
            for (Student student : students) {
                table.addCell(student.getStudentId());
                table.addCell(student.getFirstName() + " " + student.getLastName());
                table.addCell(String.valueOf(student.getCurrentGpa()));
                table.addCell(student.getAttendancePercentage() + "%");
                table.addCell(String.valueOf(student.getRiskLevel()));
            }

            document.add(table);
            document.close();

        } catch (DocumentException e) {
            e.printStackTrace();
        }

        return new ByteArrayInputStream(out.toByteArray());
    }
}