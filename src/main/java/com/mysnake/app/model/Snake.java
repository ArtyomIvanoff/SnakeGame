package com.mysnake.app.model;

import java.awt.geom.Rectangle2D;

public class Snake {
    Rectangle2D head;
    Rectangle2D[] body;

    public Rectangle2D getHead() {
        return head;
    }

    public void setHead(Rectangle2D head) {
        this.head = head;
    }

    public Rectangle2D[] getBody() {
        return body;
    }

    public void setBody(Rectangle2D[] body) {
        this.body = body;
    }

    final int basicLength = 5;

    final static int snakeWidth = 10;
    double xStart;
    double yStart;

    public enum Trends {
        NORTH, WEST, SOUTH, EAST;

        public boolean isOpposite(Trends trend) {
            switch (trend) {
                case NORTH:
                    return this.equals(SOUTH);

                case WEST:
                    return this.equals(EAST);

                case SOUTH:
                    return this.equals(NORTH);

                case EAST:
                    return this.equals(WEST);
            }

            return true;
        }
    }

    Trends trendHead;

    public Trends getTrendHead() {
        return trendHead;
    }

    public void setTrendHead(Trends trendHead) {
        if(this.trendHead != trendHead)
            this.trendHead = trendHead;
    }

    // xS = WIDTH * 0.5;
    // yS = HEIGHT * 0.5;
    public Snake(double xS, double yS) {
        xStart = xS;
        yStart = yS;

        head = new Rectangle2D.Double(xStart, yStart, snakeWidth, snakeWidth);

        body = new Rectangle2D[basicLength];
        for (int i = 0; i < basicLength; i++)
            body[i] = new Rectangle2D.Double(head.getX() - (i + 1) * snakeWidth, head.getY(), snakeWidth, snakeWidth);

        trendHead = Trends.EAST;
    }

    public void grow() {
        int size = body.length;
        Rectangle2D[] bodyTmp = new Rectangle2D[size+1];
        for (int i = 0; i < size; i++)
            bodyTmp[i] = body[i];

        bodyTmp[size] = bodyTmp[size-1];

        body = bodyTmp;
    }

    public void snakeMove() {
        for (int i = body.length - 1; i > 0; i--)
            body[i] = new Rectangle2D.Double(body[i - 1].getX(), body[i - 1].getY(), snakeWidth, snakeWidth);

        body[0] = new Rectangle2D.Double(head.getX(), head.getY(), snakeWidth, snakeWidth);

        Rectangle2D.Double head1;

        switch (trendHead) {
            case NORTH:
                head1 = new Rectangle2D.Double(head.getX(), head.getY() - snakeWidth, snakeWidth, snakeWidth);
                break;
            case WEST:
                head1 = new Rectangle2D.Double(head.getX() - snakeWidth, head.getY(), snakeWidth, snakeWidth);
                break;
            case SOUTH:
                head1 = new Rectangle2D.Double(head.getX(), head.getY() + snakeWidth, snakeWidth, snakeWidth);
                break;
            case EAST:
                head1 = new Rectangle2D.Double(head.getX() + snakeWidth, head.getY(), snakeWidth, snakeWidth);
                break;
            default:
                head1 = null;
        }

        this.setHead(head1);
    }
}
