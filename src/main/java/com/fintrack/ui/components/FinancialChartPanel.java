package com.fintrack.ui.components;

import javax.swing.BorderFactory;
import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;

/** Lightweight Java2D chart surface; it uses only real data supplied by the dashboard. */
public final class FinancialChartPanel extends RoundedPanel {
    public enum Type { DONUT, LINE, BAR, STACKED_BAR, AREA, RADAR }

    private final String title;
    private final Type type;
    private String[] labels;
    private double[][] series;
    private String[] seriesNames;
    private String emptyMessage = "No data available yet";

    public FinancialChartPanel(String title, Type type, String[] labels, double[][] series, String[] seriesNames) {
        super(AppTheme.CARD, 20, AppTheme.BORDER);
        this.title = title;
        this.type = type;
        this.labels = labels == null ? new String[0] : labels.clone();
        this.series = copySeries(series);
        this.seriesNames = seriesNames == null ? new String[0] : seriesNames.clone();
        setBorder(BorderFactory.createEmptyBorder(16, 18, 14, 18));
        setPreferredSize(new java.awt.Dimension(500, 285));
        setMinimumSize(new java.awt.Dimension(300, 260));
    }

    private static double[][] copySeries(double[][] values) {
        if (values == null) return new double[0][];
        double[][] copy = new double[values.length][];
        for (int i = 0; i < values.length; i++) copy[i] = values[i] == null ? new double[0] : values[i].clone();
        return copy;
    }

    @Override protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        Graphics2D g = (Graphics2D) graphics.create();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        int w = getWidth();
        int h = getHeight();
        g.setFont(new Font("SansSerif", Font.BOLD, 14));
        g.setColor(AppTheme.TEXT);
        g.drawString(title, 20, 27);
        g.setColor(AppTheme.BORDER);
        g.drawLine(20, 42, w - 20, 42);
        if (!hasData()) {
            g.setFont(new Font("SansSerif", Font.PLAIN, 12));
            g.setColor(AppTheme.MUTED);
            centerString(g, emptyMessage, 20, 43, w - 40, h - 48);
            g.dispose();
            return;
        }
        switch (type) {
            case DONUT -> drawDonut(g, w, h);
            case LINE -> drawLines(g, w, h);
            case BAR -> drawBars(g, w, h);
            case STACKED_BAR -> drawStackedBars(g, w, h);
            case AREA -> drawArea(g, w, h);
            case RADAR -> drawRadar(g, w, h);
        }
        g.dispose();
    }

    private boolean hasData() {
        if (labels.length == 0 || series.length == 0) return false;
        for (double[] s : series) if (s != null && s.length > 0) {
            for (double v : s) if (Double.isFinite(v) && v != 0d) return true;
        }
        return false;
    }

    private void drawDonut(Graphics2D g, int w, int h) {
        double total = 0;
        for (double v : series[0]) total += Math.max(0, v);
        if (total <= 0) { drawEmpty(g, w, h, "No expense transactions recorded yet"); return; }
        int size = Math.max(100, Math.min(h - 78, (int) (w * 0.43)));
        int x = 30, y = 54 + Math.max(0, (h - 65 - size) / 2);
        double start = 90;
        for (int i = 0; i < Math.min(labels.length, series[0].length); i++) {
            double value = Math.max(0, series[0][i]);
            if (value <= 0) continue;
            double extent = value / total * 360.0;
            g.setColor(AppTheme.CHART_COLORS[i % AppTheme.CHART_COLORS.length]);
            g.fill(new Arc2D.Double(x, y, size, size, start, -extent, Arc2D.PIE));
            start -= extent;
        }
        int hole = (int) (size * 0.60);
        g.setColor(AppTheme.CARD);
        g.fill(new Ellipse2D.Double(x + (size - hole) / 2.0, y + (size - hole) / 2.0, hole, hole));
        g.setFont(new Font("SansSerif", Font.BOLD, 13));
        g.setColor(AppTheme.TEXT);
        centerString(g, "Expenses", x + (size - hole) / 2, y + size / 2 - 3, hole, 20);
        g.setFont(new Font("SansSerif", Font.PLAIN, 11));
        g.setColor(AppTheme.MUTED);
        centerString(g, compactMoney(total), x + (size - hole) / 2, y + size / 2 + 8, hole, 20);
        int lx = x + size + 22;
        int ly = 68;
        int maxLegend = Math.min(labels.length, 7);
        FontMetrics fm = g.getFontMetrics(new Font("SansSerif", Font.PLAIN, 11));
        for (int i = 0; i < maxLegend; i++) {
            if (series[0][i] <= 0) continue;
            g.setColor(AppTheme.CHART_COLORS[i % AppTheme.CHART_COLORS.length]);
            g.fillRoundRect(lx, ly - 8, 9, 9, 4, 4);
            g.setColor(AppTheme.TEXT);
            String label = trimToWidth(labels[i], fm, Math.max(60, w - lx - 90));
            g.drawString(label, lx + 15, ly);
            g.setColor(AppTheme.MUTED);
            g.drawString(compactMoney(series[0][i]), Math.min(w - 75, lx + 15 + fm.stringWidth(label) + 5), ly + 13);
            ly += 27;
        }
        if (labels.length > maxLegend) {
            g.setColor(AppTheme.MUTED);
            g.drawString("+ " + (labels.length - maxLegend) + " more categories", lx, ly + 3);
        }
    }

    private void drawLines(Graphics2D g, int w, int h) {
        Plot p = plot(w, h, 49, 20, 57, 43);
        double max = maxAbs(series);
        if (max <= 0) max = 1;
        drawGrid(g, p, 0, max, false);
        int count = Math.max(1, labels.length);
        for (int s = 0; s < series.length; s++) {
            g.setColor(AppTheme.CHART_COLORS[s % AppTheme.CHART_COLORS.length]);
            g.setStroke(new BasicStroke(2.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            double[] values = series[s];
            Path2D path = new Path2D.Double();
            for (int i = 0; i < Math.min(count, values.length); i++) {
                int x = p.x + (count == 1 ? p.w / 2 : i * p.w / (count - 1));
                int y = p.y + p.h - (int) (Math.max(0, values[i]) / max * p.h);
                if (i == 0) path.moveTo(x, y); else path.lineTo(x, y);
            }
            g.draw(path);
            for (int i = 0; i < Math.min(count, values.length); i++) {
                int x = p.x + (count == 1 ? p.w / 2 : i * p.w / (count - 1));
                int y = p.y + p.h - (int) (Math.max(0, values[i]) / max * p.h);
                g.fill(new Ellipse2D.Double(x - 3.5, y - 3.5, 7, 7));
            }
        }
        drawXLabels(g, p, labels);
        drawLegend(g, seriesNames, Math.max(56, w - 175), 59);
    }

    private void drawBars(Graphics2D g, int w, int h) {
        Plot p = plot(w, h, 48, 20, 52, 43);
        double min = 0, max = 0;
        for (double v : series[0]) { min = Math.min(min, v); max = Math.max(max, v); }
        if (Math.abs(max - min) < 0.0001) { max = 1; min = 0; }
        double range = max - min;
        drawGrid(g, p, min, max, true);
        int count = Math.max(1, labels.length);
        int slot = p.w / count;
        int barW = Math.max(12, Math.min(34, slot - 16));
        int zeroY = p.y + (int) ((max / range) * p.h);
        g.setColor(AppTheme.MUTED);
        g.setStroke(new BasicStroke(1f));
        g.drawLine(p.x, zeroY, p.x + p.w, zeroY);
        for (int i = 0; i < Math.min(count, series[0].length); i++) {
            int x = p.x + i * slot + (slot - barW) / 2;
            int yVal = p.y + (int) ((max - series[0][i]) / range * p.h);
            int top = Math.min(yVal, zeroY);
            int bh = Math.max(2, Math.abs(zeroY - yVal));
            g.setColor(series[0][i] < 0 ? AppTheme.RED : AppTheme.TEAL);
            g.fillRoundRect(x, top, barW, bh, 8, 8);
        }
        drawXLabels(g, p, labels);
    }

    private void drawStackedBars(Graphics2D g, int w, int h) {
        Plot p = plot(w, h, 48, 20, 56, 48);
        int count = Math.max(1, labels.length);
        double max = 0;
        for (int i = 0; i < count; i++) {
            double sum = 0;
            for (double[] s : series) if (i < s.length) sum += Math.max(0, s[i]);
            max = Math.max(max, sum);
        }
        if (max <= 0) max = 1;
        drawGrid(g, p, 0, max, false);
        int slot = p.w / count;
        int barW = Math.max(13, Math.min(38, slot - 15));
        for (int i = 0; i < count; i++) {
            int x = p.x + i * slot + (slot - barW) / 2;
            int y = p.y + p.h;
            for (int s = 0; s < series.length; s++) {
                double val = i < series[s].length ? Math.max(0, series[s][i]) : 0;
                int segment = (int) (val / max * p.h);
                y -= segment;
                if (segment > 0) {
                    g.setColor(s == 2 ? AppTheme.RED : AppTheme.CHART_COLORS[s % AppTheme.CHART_COLORS.length]);
                    g.fillRoundRect(x, y, barW, segment + (s == 0 ? 4 : 0), 7, 7);
                }
            }
        }
        drawXLabels(g, p, labels);
        drawLegend(g, seriesNames, Math.max(52, w - 235), 59);
    }

    private void drawArea(Graphics2D g, int w, int h) {
        Plot p = plot(w, h, 51, 20, 56, 43);
        double min = 0, max = 0;
        for (double v : series[0]) { min = Math.min(min, v); max = Math.max(max, v); }
        if (Math.abs(max - min) < 0.0001) { max = min + 1; }
        double range = max - min;
        drawGrid(g, p, min, max, true);
        int count = Math.min(labels.length, series[0].length);
        if (count == 0) return;
        Path2D area = new Path2D.Double();
        for (int i = 0; i < count; i++) {
            int x = p.x + (count == 1 ? p.w / 2 : i * p.w / (count - 1));
            int y = p.y + (int) ((max - series[0][i]) / range * p.h);
            if (i == 0) { area.moveTo(x, p.y + p.h); area.lineTo(x, y); } else area.lineTo(x, y);
        }
        int lastX = p.x + (count == 1 ? p.w / 2 : (count - 1) * p.w / (count - 1));
        area.lineTo(lastX, p.y + p.h); area.closePath();
        g.setComposite(AlphaComposite.SrcOver.derive(0.20f));
        g.setColor(AppTheme.PRIMARY);
        g.fill(area);
        g.setComposite(AlphaComposite.SrcOver);
        g.setColor(AppTheme.PRIMARY);
        g.setStroke(new BasicStroke(2.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        Path2D line = new Path2D.Double();
        for (int i = 0; i < count; i++) {
            int x = p.x + (count == 1 ? p.w / 2 : i * p.w / (count - 1));
            int y = p.y + (int) ((max - series[0][i]) / range * p.h);
            if (i == 0) line.moveTo(x, y); else line.lineTo(x, y);
        }
        g.draw(line);
        drawXLabels(g, p, labels);
    }

    private void drawRadar(Graphics2D g, int w, int h) {
        int count = Math.min(labels.length, series[0].length);
        if (count < 3) { drawEmpty(g, w, h, "Add more financial data to see this snapshot"); return; }
        int cx = w / 2;
        int cy = (h + 42) / 2;
        int radius = Math.max(48, Math.min((h - 112) / 2, (w - 180) / 2));
        for (int ring = 1; ring <= 4; ring++) {
            Polygon p = new Polygon();
            double r = radius * ring / 4.0;
            for (int i = 0; i < count; i++) {
                double a = -Math.PI / 2 + 2 * Math.PI * i / count;
                p.addPoint(cx + (int) (Math.cos(a) * r), cy + (int) (Math.sin(a) * r));
            }
            g.setColor(AppTheme.BORDER); g.setStroke(new BasicStroke(1f)); g.drawPolygon(p);
        }
        Polygon values = new Polygon();
        for (int i = 0; i < count; i++) {
            double a = -Math.PI / 2 + 2 * Math.PI * i / count;
            double score = Math.max(0, Math.min(100, series[0][i]));
            values.addPoint(cx + (int) (Math.cos(a) * radius * score / 100.0), cy + (int) (Math.sin(a) * radius * score / 100.0));
            int lx = cx + (int) (Math.cos(a) * (radius + 22));
            int ly = cy + (int) (Math.sin(a) * (radius + 22));
            g.setColor(AppTheme.MUTED); g.setFont(new Font("SansSerif", Font.PLAIN, 10));
            String label = labels[i];
            FontMetrics fm = g.getFontMetrics();
            if (lx < cx - 5) lx -= fm.stringWidth(label);
            else if (Math.abs(lx - cx) <= 5) lx -= fm.stringWidth(label) / 2;
            g.drawString(label, lx, ly);
        }
        g.setColor(new Color(AppTheme.PRIMARY.getRed(), AppTheme.PRIMARY.getGreen(), AppTheme.PRIMARY.getBlue(), 45));
        g.fillPolygon(values);
        g.setColor(AppTheme.PRIMARY); g.setStroke(new BasicStroke(2f)); g.drawPolygon(values);
        for (int i = 0; i < values.npoints; i++) g.fill(new Ellipse2D.Double(values.xpoints[i] - 3, values.ypoints[i] - 3, 6, 6));
    }

    private void drawGrid(Graphics2D g, Plot p, double min, double max, boolean includeNegative) {
        g.setStroke(new BasicStroke(1f));
        g.setFont(new Font("SansSerif", Font.PLAIN, 10));
        FontMetrics fm = g.getFontMetrics();
        for (int i = 0; i <= 4; i++) {
            int y = p.y + i * p.h / 4;
            g.setColor(AppTheme.GRID); g.drawLine(p.x, y, p.x + p.w, y);
            double v = max - i * (max - min) / 4.0;
            String label = compactMoney(v);
            g.setColor(AppTheme.MUTED); g.drawString(label, Math.max(1, p.x - fm.stringWidth(label) - 8), y + 4);
        }
    }

    private void drawXLabels(Graphics2D g, Plot p, String[] labels) {
        if (labels.length == 0) return;
        g.setFont(new Font("SansSerif", Font.PLAIN, 10));
        g.setColor(AppTheme.MUTED);
        FontMetrics fm = g.getFontMetrics();
        int count = labels.length;
        int shown = Math.min(count, 7);
        for (int n = 0; n < shown; n++) {
            int i = shown == 1 ? 0 : (int) Math.round(n * (count - 1.0) / (shown - 1.0));
            int x = p.x + (count == 1 ? p.w / 2 : i * p.w / (count - 1));
            String label = trimToWidth(labels[i], fm, 60);
            g.drawString(label, x - fm.stringWidth(label) / 2, p.y + p.h + 18);
        }
    }

    private void drawLegend(Graphics2D g, String[] names, int x, int y) {
        if (names == null || names.length == 0) return;
        g.setFont(new Font("SansSerif", Font.PLAIN, 10));
        int used = 0;
        for (int i = 0; i < names.length && used < 3; i++) {
            int xx = x + used * 82;
            g.setColor(AppTheme.CHART_COLORS[i % AppTheme.CHART_COLORS.length]);
            g.fillRoundRect(xx, y - 7, 8, 8, 4, 4);
            g.setColor(AppTheme.MUTED);
            g.drawString(trimToWidth(names[i], g.getFontMetrics(), 70), xx + 12, y);
            used++;
        }
    }

    private Plot plot(int w, int h, int left, int right, int top, int bottom) {
        return new Plot(left, 52 + top, Math.max(40, w - left - right), Math.max(60, h - 52 - top - bottom));
    }

    private double maxAbs(double[][] all) {
        double max = 0;
        for (double[] values : all) for (double value : values) max = Math.max(max, value);
        return max;
    }

    private void drawEmpty(Graphics2D g, int w, int h, String text) {
        g.setFont(new Font("SansSerif", Font.PLAIN, 12)); g.setColor(AppTheme.MUTED);
        centerString(g, text, 20, 43, w - 40, h - 48);
    }

    private void centerString(Graphics2D g, String value, int x, int y, int w, int h) {
        FontMetrics fm = g.getFontMetrics();
        g.drawString(value, x + Math.max(0, (w - fm.stringWidth(value)) / 2), y + h / 2 + fm.getAscent() / 2 - 2);
    }

    private String compactMoney(double amount) {
        double abs = Math.abs(amount);
        if (abs >= 10_000_000) return (amount < 0 ? "−₹" : "₹") + String.format(Locale.US, "%.1fCr", abs / 10_000_000);
        if (abs >= 100_000) return (amount < 0 ? "−₹" : "₹") + String.format(Locale.US, "%.1fL", abs / 100_000);
        if (abs >= 1_000) return (amount < 0 ? "−₹" : "₹") + String.format(Locale.US, "%.1fk", abs / 1_000);
        return (amount < 0 ? "−₹" : "₹") + NumberFormat.getNumberInstance(Locale.forLanguageTag("en-IN")).format(Math.round(abs));
    }

    private String trimToWidth(String value, FontMetrics fm, int maxWidth) {
        if (value == null) return "";
        if (fm.stringWidth(value) <= maxWidth) return value;
        String shortValue = value;
        while (!shortValue.isEmpty() && fm.stringWidth(shortValue + "…") > maxWidth) shortValue = shortValue.substring(0, shortValue.length() - 1);
        return shortValue + "…";
    }

    private record Plot(int x, int y, int w, int h) { }
}
