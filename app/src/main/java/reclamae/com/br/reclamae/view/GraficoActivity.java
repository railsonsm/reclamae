package reclamae.com.br.reclamae.view;

import android.graphics.Color;
import android.graphics.Paint;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.LargeValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import reclamae.com.br.reclamae.R;
import reclamae.com.br.reclamae.dao.ReclamacaoDao;
import reclamae.com.br.reclamae.dao.SugestaoDao;
import reclamae.com.br.reclamae.model.Reclamacao;

public class GraficoActivity extends AppCompatActivity {
    WebView wvGrafico;
    WebView wvGrafico2;
    String strURL1;
    String strURL2;

    public static int rgb(String hex) {
        int color = (int) Long.parseLong(hex.replace("#", ""), 16);
        int r = (color >> 16) & 0xFF;
        int g = (color >> 8) & 0xFF;
        int b = (color >> 0) & 0xFF;
        return Color.rgb(r, g, b);
    }

    public static final int[] CAT_REC = {
           rgb("#FF0000"), rgb("#7CFC00")
    };

    public static final int[] REC_CAT= {
            rgb("#FF0000"), rgb("#7CFC00"),rgb("#FF0000"), rgb("#7CFC00"), rgb("#FF0000")
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grafico);

        //gerarGrafico();
/*
        strURL1 = "https://chart.googleapis.com/chart?" +
                "cht=lc&" + //define o tipo do gráfico "linha"
                "chxt=x,y&" + //imprime os valores dos eixos X, Y
                "chs=300x300&" + //define o tamanho da imagem
                "chd=t:10,45,5,10,13,26&" + //valor de cada coluna do gráfico
                "chl=Jan|Fev|Mar|Abr|Mai|Jun&" + //rótulo para cada coluna
                "chdl=Vendas&" + //legenda do gráfico
                "chxr=1,0,50&" + //define o valor de início e fim do eixo
                "chds=0,50&" + //define o valor de escala dos dados
                "chg=0,5,0,0&" + //desenha linha horizontal na grade
                "chco=3D7930&" + //cor da linha do gráfico
                "chtt=Vendas+x+1000&" + //cabeçalho do gráfico
                "chm=B,C5D4B5BB,0,0,0"; //fundo verde
        strURL2 = "https://chart.googleapis.com/chart?" +
                "cht=lc&" + //define o tipo do gráfico "linha"
                "chxt=x,y&" + //imprime os valores dos eixos X, Y
                "chs=300x300&" + //define o tamanho da imagem
                "chd=t:20,35,10,15,18,30&" + //valor de cada coluna do gráfico
                "chl=Jan|Fev|Mar|Abr|Mai|Jun&" + //rótulo para cada coluna
                "chdl=Vendas&" + //legenda do gráfico
                "chxr=1,0,50&" + //define o valor de início e fim do eixo
                "chds=0,50&" + //define o valor de escala dos dados
                "chg=0,5,0,0&" + //desenha linha horizontal na grade
                "chco=3D7930&" + //cor da linha do gráfico
                "chtt=Vendas+x+1000&" + //cabeçalho do gráfico
                "chm=B,C5D4B5BB,0,0,0"; //fundo verde

        wvGrafico = (WebView)findViewById(R.id.wvGrafico);
        wvGrafico2 = (WebView)findViewById(R.id.wvGrafico);
        wvGrafico.loadUrl(strURL1);
        wvGrafico2.loadUrl(strURL2);*/
        gerarGraficoReclaSuge();
        gerarGraficoReclamacoesPorCategoria();
    }

    private void gerarGraficoReclaSuge() {
        ReclamacaoDao reclamacaoDao = new ReclamacaoDao(GraficoActivity.this);

        SugestaoDao sugestaoDao = new SugestaoDao(GraficoActivity.this);
        reclamacaoDao.contaReclamacoesPorCategoria();
        Long reclamacaoes = reclamacaoDao.contaReclamacoes();
        Long sugestoes = sugestaoDao.contaSugestoes();
        List<PieEntry> pieEntryList= new ArrayList<>();
        pieEntryList.add(new PieEntry(reclamacaoes, "Reclamações"));
        pieEntryList.add(new PieEntry(sugestoes, "Sugestões"));
        PieDataSet dataSet = new PieDataSet(pieEntryList, "");
        dataSet.setColors(CAT_REC);
        PieData data = new PieData(dataSet);

        //buscar dados
        PieChart chart = (PieChart) findViewById(R.id.chartRS);
        Description descricao = new Description();
        descricao.setText("Gráfico com a contagem de reclamações/sugestões. Dados atualizados até: " + new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new Date()));
        chart.setDescription(descricao);
        chart.getDescription().setPosition(690,12);
        chart.setData(data);
        chart.getLegend().setEnabled(false);
        chart.setEntryLabelColor(Color.BLACK);
        chart.invalidate();
    }



    private BarChart chart;
    float barWidth;
    float barSpace;
    float groupSpace;

    private void gerarGraficoReclamacoesPorCategoria(){

        int groupCount = 6;

        barWidth = 0.4f;
        barSpace = 0f;
        groupSpace = 0.2f;

        ReclamacaoDao reclamacaoDao = new ReclamacaoDao(GraficoActivity.this);
        List<Reclamacao> reclamacaos = reclamacaoDao.contaReclamacoesPorCategoria();
        ArrayList<String> labes = new ArrayList<>();
        List<BarEntry> barEntries= new ArrayList<>();
        for (int i=0; i<reclamacaos.size(); i++){


            switch (reclamacaos.get(i).getCategoria()){
                case "Saúde":
                    REC_CAT[i] = rgb("FF0000");
                    break;
                case "Sanamento":
                    REC_CAT[i] = rgb("#FFA500");
                    break;
                case "Educação":
                    REC_CAT[i] = rgb("#6495ED");
                    break;
                case "Limpeza":
                    REC_CAT[i] = rgb("#FF00FF");
                    break;
                case "Segurança":
                    REC_CAT[i] = rgb("#EE82EE");
                    break;
            }
            labes.add(reclamacaos.get(i).getCategoria());
            barEntries.add(new BarEntry(i+1, reclamacaos.get(i).getId(), reclamacaos.get(i).getCategoria().toString()));
        }
        BarDataSet dataSet = new BarDataSet(barEntries, "");
         dataSet.setColors(REC_CAT);

        BarChart chart = (BarChart)findViewById(R.id.chartReclamaoCat);
        chart.setDescription(null);
        chart.setPinchZoom(false);
        chart.setScaleEnabled(false);
        chart.setDrawBarShadow(false);
        chart.setDrawGridBackground(false);


        BarData data = new BarData(dataSet, dataSet);
        data.setValueFormatter(new LargeValueFormatter());
        chart.setData(data);
        chart.getBarData().setBarWidth(barWidth);
        chart.getXAxis().setAxisMinimum(0);
        chart.getXAxis().setCenterAxisLabels(true);
        chart.getXAxis().setAxisMaximum(0 + 15);
        chart.groupBars(0, groupSpace, barSpace);
        chart.getData().setHighlightEnabled(false);

        Description descricao = new Description();
        descricao.setText("Gráfico de reclamações agrupadas por categoria. Dados atualizados até: " + new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new Date()));
        chart.setDescription(descricao);
        chart.getDescription().setPosition(680,15);
        chart.invalidate();

        chart.getLegend().setEnabled(false);

        //X-axis
        XAxis xAxis = chart.getXAxis();
        xAxis.setGranularity(1f);
        xAxis.setGranularityEnabled(true);
        xAxis.setCenterAxisLabels(true);
        xAxis.setDrawGridLines(false);
        xAxis.setAxisMaximum(5);
      //  xAxis.setCenterAxisLabels(true);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setValueFormatter(new IndexAxisValueFormatter(labes));

//Y-axis

    }
}

 /*
    private void gerarGraficoReclamacoesPorCategoria(){
        ReclamacaoDao reclamacaoDao = new ReclamacaoDao(GraficoActivity.this);
        List<Reclamacao> reclamacaos = reclamacaoDao.contaReclamacoesPorCategoria();

        List<PieEntry> pieEntryList= new ArrayList<>();
        for (int i=0; i<reclamacaos.size(); i++){


            switch (reclamacaos.get(i).getCategoria()){
                case "Saúde":
                    REC_CAT[i] = rgb("FF0000");
                    break;
                case "Sanamento":
                    REC_CAT[i] = rgb("#FFA500");
                    break;
                case "Educação":
                    REC_CAT[i] = rgb("#6495ED");
                    break;
                case "Limpeza":
                    REC_CAT[i] = rgb("#FF00FF");
                    break;
                case "Segurança":
                    REC_CAT[i] = rgb("#EE82EE");
                    break;
            }
           pieEntryList.add(new PieEntry(reclamacaos.get(i).getId(), reclamacaos.get(i).getCategoria()));
        }
        PieDataSet dataSet = new PieDataSet(pieEntryList, "");
        dataSet.setColors(REC_CAT);
        PieData data = new PieData(dataSet);

        //buscar dados
        PieChart chart = (PieChart) findViewById(R.id.chartReclamaoCat);
        chart.setData(data);
        chart.setEntryLabelColor(Color.BLACK);
        Description descricao = new Description();
        descricao.setText("Dados atualizados até: " + new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new Date()));
        chart.setDescription(descricao);
        chart.invalidate();
     }*/