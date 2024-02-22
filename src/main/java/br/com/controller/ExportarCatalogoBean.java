package br.com.controller;


import br.com.constante.TipoArquivo;
import br.com.model.Produto;
import com.itextpdf.text.BadElementException;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import lombok.Getter;
import lombok.Setter;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFClientAnchor;
import org.apache.poi.xssf.usermodel.XSSFDrawing;
import org.apache.poi.xssf.usermodel.XSSFPicture;
import org.apache.poi.xssf.usermodel.XSSFShape;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import javax.enterprise.context.SessionScoped;
import javax.enterprise.inject.Model;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static br.com.constante.ColunaProduto.CODIGO;
import static br.com.constante.ColunaProduto.DESCRICAO;
import static br.com.constante.ColunaProduto.PRECO;
import static br.com.constante.ColunaProduto.QTDE;

@Getter
@Setter
@Model
@SessionScoped
public class ExportarCatalogoBean implements Serializable {
    private List<Produto> produtos = new LinkedList<>();
    private List<Produto> produtosFiltrados = new ArrayList<>();
    private Map<Integer, byte[]> imagemProdutos = new LinkedHashMap<>();

    private String busca;


    public void processa(FileUploadEvent event) {
        this.produtos = new LinkedList<>();
        this.imagemProdutos = new LinkedHashMap<>();
        try {
            Workbook workbook;
            if (event.getFile().getContentType().equals(TipoArquivo.XLSX)) {
                workbook = new HSSFWorkbook(event.getFile().getInputStream());
            } else {
                workbook = new XSSFWorkbook(event.getFile().getInputStream());
            }

            Sheet sheet = workbook.getSheetAt(0);
            Row row = sheet.getRow(4);

            XSSFDrawing drawing = (XSSFDrawing) sheet.createDrawingPatriarch();
            for (XSSFShape shape : drawing.getShapes()) {
                if (shape instanceof XSSFPicture) {
                    XSSFPicture xssfPicture = ((XSSFPicture) shape);
                    XSSFClientAnchor anchor = ((XSSFPicture) shape).getClientAnchor();
                    imagemProdutos.put(anchor.getRow1(), xssfPicture.getPictureData().getData());

                }
            }

            for (int i = row.getRowNum(); i <= sheet.getLastRowNum() - 1; i++) {
                byte[] bytesImagem = imagemProdutos.get(i);
                produtos.add(new Produto(String.valueOf(i),
                        bytesImagem
                        , sheet.getRow(i).getCell(CODIGO).getStringCellValue()
                        , sheet.getRow(i).getCell(DESCRICAO).getStringCellValue()
                        , sheet.getRow(i).getCell(PRECO).getNumericCellValue()
                        , sheet.getRow(i).getCell(QTDE).getStringCellValue()
                ));
            }
            this.produtosFiltrados.addAll(produtos);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void onChangeProduto() {
        if (Objects.isNull(getBusca()) || getBusca().isEmpty()) {
            this.produtosFiltrados = new ArrayList<>(getProdutos());
        } else {
            this.produtosFiltrados = getProdutos().stream().filter(produto -> produto.getDescricao().toUpperCase().trim().contains(getBusca().toUpperCase().trim())
            || produto.getCodigo().toUpperCase().trim().contains(getBusca().toUpperCase().trim())).collect(Collectors.toList());
        }
    }

    public StreamedContent exportar() {
        try {
            ByteArrayOutputStream boas = new ByteArrayOutputStream();
            Document document = new Document();
            PdfWriter.getInstance(document, boas);

            document.open();
            document.add(new Chunk(""));


            PdfPTable produtos = new PdfPTable(3);
            produtos.setWidthPercentage(100);
            produtos.setSpacingBefore(20);
            produtos.setSpacingAfter(20);

            getProdutos().forEach(produto -> {
                PdfPCell celulaProduto = new PdfPCell();

                String codigo = produto.getCodigo();
                byte[] imagem = produto.getImagem();
                String descricao = produto.getDescricao();
                BigDecimal preco = produto.getPreco();
                String qtde = produto.getQtde();

                if (Objects.nonNull(imagem)) {
                    try {
                        Image imageData = Image.getInstance(imagem);
                        imageData.scaleToFit(100, 100);
                        imageData.setAlignment(Element.ALIGN_CENTER);
                        celulaProduto.addElement(imageData);
                    } catch (BadElementException | IOException e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    Paragraph vazioParagraph = new Paragraph("Sem imagem", new Font(Font.FontFamily.HELVETICA, 12));
                    vazioParagraph.setSpacingAfter(80);
                    vazioParagraph.setAlignment(Element.ALIGN_CENTER);
                    celulaProduto.addElement(vazioParagraph);
                }

                Paragraph codigoParagraph = new Paragraph(codigo, new Font(Font.FontFamily.HELVETICA, 12));
                codigoParagraph.setAlignment(Element.ALIGN_CENTER);
                celulaProduto.addElement(codigoParagraph);

                Paragraph descricaoParagraph = new Paragraph(descricao, new Font(Font.FontFamily.HELVETICA, 12));
                descricaoParagraph.setAlignment(Element.ALIGN_CENTER);
                celulaProduto.addElement(descricaoParagraph);

                Paragraph qtdeParagraph = new Paragraph(qtde, new Font(Font.FontFamily.HELVETICA, 12));
                qtdeParagraph.setAlignment(Element.ALIGN_CENTER);
                celulaProduto.addElement(qtdeParagraph);

                NumberFormat format = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));


                Paragraph precoParagraph = new Paragraph(format.format(preco.setScale(2, RoundingMode.HALF_UP)), new Font(FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12)));
                precoParagraph.setAlignment(Element.ALIGN_CENTER);
                celulaProduto.addElement(precoParagraph);

                celulaProduto.setVerticalAlignment(Element.ALIGN_BOTTOM);
                celulaProduto.setHorizontalAlignment(Element.ALIGN_BOTTOM);
                produtos.addCell(celulaProduto);
            });

            document.add(produtos);
            document.close();

            return DefaultStreamedContent
                    .builder()
                    .contentType("application/pdf")
                    .name("catalogo.pdf")
                    .stream(() -> new ByteArrayInputStream(boas.toByteArray()))
                    .build();
        } catch (DocumentException e) {
            throw new RuntimeException(e);
        }
    }
}
