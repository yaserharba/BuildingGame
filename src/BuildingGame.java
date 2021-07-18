import processing.core.*;

public class BuildingGame extends PApplet {
    public static void main(String[] args) {
        PApplet.main("BuildingGame", args);
    }

    final int screenWidth = 500;
    final int screenHeight = 700;
    int score;
    int maxScore;
    Building building;
    NextBlock nextBlock;
    BackGround[] backGrounds;
    boolean readyToMove;

    public void setup() {
        backGrounds = new BackGround[4];
        readyToMove = false;
        maxScore = 0;
        reStart();
    }

    public void settings() {
        size(screenWidth, screenHeight);
    }

    public void draw() {
        readyToMove = false;
        background(0, 0, 100);
        if (building.last().y < 350) {
            frameRate(60);
            for (BackGround backGround : backGrounds) backGround.moveUp();
            building.moveUp();
        } else if (building.last().y - nextBlock.block.y < 110) {
            frameRate(40);
            nextBlock.block.y -= 5;
        } else {
            readyToMove = true;
            frameRate(building.tall() * 2 + 40);
            nextBlock.moveNewBlock();
        }
        for (BackGround backGround : backGrounds) backGround.show();
        nextBlock.show();
        building.show();
        showScore();
    }

    public void showScore() {
        //score = building.tall() - 1;
        maxScore = max(maxScore, score);
        fill(255);
        textAlign(CENTER, CENTER);
        textSize(40);
        text("Score:", (float) screenWidth / 4, 50);
        text(score, (float) screenWidth / 4, 100);
        text("Max Score:", 3 * (float) screenWidth / 4, 50);
        text(maxScore, 3 * (float) screenWidth / 4, 100);
    }


    public void keyPressed() {
        if (key == ' ' && readyToMove) {
            Block prev = building.last();
            Block next = nextBlock.block;
            int stx = max(prev.x - prev.width / 2, next.x - next.width / 2);
            int enx = min(prev.x + prev.width / 2, next.x + next.width / 2);
            if (enx <= stx) {
                reStart();
            } else {
                int lastx = next.x;
                int lasty = next.y;
                next.y = prev.y - prev.height / 2 - next.height / 2;
                next.x = (enx + stx) / 2;
                next.width = enx - stx;
                score += next.width;
                building.addBlock(next);
                nextBlock.pickNew(lastx, lasty);
            }
        }
    }

    void reStart() {
        score = 0;
        building = new Building();
        nextBlock = new NextBlock(screenWidth / 2, screenHeight - 120);
        backGrounds[0] = new BackGround(80, screenHeight, 20, 0, 0, 0, 255, 0);
        int last = backGrounds[0].lastHeight() + backGrounds[0].width;
        backGrounds[1] = new BackGround(250, last, 0, 255, 255, 0, 0, 255);
        last = backGrounds[1].lastHeight() + backGrounds[1].width;
        backGrounds[2] = new BackGround(300, last, 0, 0, 255, 0, 0, 0);
        last = backGrounds[2].lastHeight() + backGrounds[2].width;
        backGrounds[3] = new BackGround(300, last, 0, 0, 0, 0, 0, 100);
    }

    class BackGround {
        int[] array;
        int sz;
        int width = 5;
        int start;
        int sr, sg, sb;
        int er, eg, eb;
        int last;

        public BackGround(int sz, int start, int sr, int sg, int sb,
                          int er, int eg, int eb) {
            this.sz = sz;
            this.start = start;
            array = new int[sz];
            for (int i = 0; i < sz; i++) {
                last = array[i] = this.start - (width * i);
            }
            this.sr = sr;
            this.sg = sg;
            this.sb = sb;
            this.er = er;
            this.eg = eg;
            this.eb = eb;
        }

        public void show() {
            strokeWeight(width);
            for (int i = 0; i < sz; i++) {
                if (array[i] < 0)
                    break;
                stroke(map(i, 0, sz, sr, er), map(i, 0, sz, sg, eg), map(i, 0, sz, sb, eb));
                line(0, array[i], screenWidth, array[i]);
            }
        }

        public void moveUp() {
            for (int i = 0; i < sz; i++) {
                array[i] += 1;
            }
        }

        public int lastHeight() {
            return last;
        }
    }

    class NextBlock {
        Block block;
        final float m = 0.06f;
        float p;
        int lastFrameCount;

        public NextBlock(int x, int y) {
            this.block = new Block(x, y);
            this.lastFrameCount = frameCount;
            this.p = 0;
        }

        void pickNew(int x, int y) {
            this.block = new Block(x, y);
        }

        public void moveNewBlock() {
            this.p += m * (this.lastFrameCount - frameCount + 1);
            this.block.x = (int) (120 * sin(this.m * frameCount + this.p) + 250);
            this.lastFrameCount = frameCount;
        }

        void show() {
            this.block.show();
        }
    }

    class Building {
        Block[] building;

        public Building() {
            building = new Block[1];
            building[0] = new Block(250, screenHeight - 5);
            building[0].height = 10;
        }

        public void addBlock(Block newBlock) {
            building = (Block[]) append(building, newBlock);
        }

        public Block last() {
            return building[building.length - 1];
        }

        public void moveUp() {
            for (Block block : building) {
                block.y += 1;
            }
        }

        public void show() {
            for (int i = building.length - 1; i >= 0; i--) {
                building[i].show();
                if (building[i].y - building[i].height / 2 > screenHeight)
                    break;
            }
        }

        public int tall() {
            return building.length;
        }
    }

    class Block {
        int x, y;
        int width, height;
        int blockColor;

        public Block(int x, int y) {
            this.x = x;
            this.y = y;
            width = 100;
            height = 50;
            colorMode(HSB);
            blockColor = color(random(255), 200, 200);
            colorMode(RGB);
        }

        public void show() {
            rectMode(CENTER);
            fill(blockColor);
            strokeWeight(1);
            stroke(0);
            rect(x, y, width, height);
        }
    }
}
