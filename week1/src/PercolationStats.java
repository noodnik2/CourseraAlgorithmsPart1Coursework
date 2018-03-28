
import edu.princeton.cs.algs4.StdRandom;
import edu.princeton.cs.algs4.StdStats;

/**
 *  First Programming Assignment - "PercolationStats"
 *  Coursera Princeton "Algorithms" course (Feb 2018)
 *  <p />
 *  Accepts two arguments n and T, performs T independent
 *  computational experiments (discussed in accompanying web page)
 *  on an n-by-n grid, and prints the sample mean, sample standard
 *  deviation, and the 95% confidence interval for the
 *  percolation threshold.
 *  @author mross
 *  @see <a
 *  href="http://coursera.cs.princeton.edu/algs4/assignments/percolation.html"
 *  >Programming Assignment 1: Percolation</a>
 */
public class PercolationStats {


    //
    //  Private instance data
    //

    /** value returned by similarly named method. */
    private final double mMean;

    /** value returned by similarly named method. */
    private final double mStddev;

    /** value returned by similarly named method. */
    private final double mConfidenceLo;

    /** value returned by similarly named method. */
    private final double mConfidenceHi;
    

    //
    //  Public class methods
    //

    /**
     *  Test client; performs T independent computational experiments
     *  (discussed on web page) on an n-by-n grid, and prints the sample
     *  mean, sample standard deviation, and the 95% confidence interval
     *  for the percolation threshold.
     *  @param inArgs two numbers: {@code n} and {@code T}
     */
    public static void main(final String[] inArgs) {
        if (inArgs.length != 2) {
            throw new IllegalArgumentException("please specify n and T");
        }
        final int n = Integer.parseInt(inArgs[0]);
        final int t = Integer.parseInt(inArgs[1]);
        final PercolationStats ps = new PercolationStats(n, t);
        System.out.format("mean                    = %s\n", ps.mean());
        System.out.format("stddev                  = %s\n", ps.stddev());
        System.out.format(
            "95%% confidence interval = [%s, %s]\n",
            ps.confidenceLo(),
            ps.confidenceHi()
        );
    }


    //
    //  Public instance construction
    //

    /**
     *  Performs "percolation" trials (independent experiments)
     *  on an n-by-n grid.
     *  @param inN size of percolation matrix
     *  @param inTrials number of trials to run
     *  @throws IllegalArgumentException if either n ≤ 0 or trials ≤ 0
     */
    public PercolationStats(final int inN, final int inTrials) {

        if (inN < 1 || inTrials < 1) {
            throw new IllegalArgumentException();
        }

        final double[] openSitePct = new double[inTrials];
        final double nSitesTotal = (double) (inN * inN);

        for (int t = 0; t < inTrials; t++) {
            final Percolation p = new Percolation(inN);
            while (!p.percolates()) {
                p.open(
                    StdRandom.uniform(1, inN + 1),
                    StdRandom.uniform(1, inN + 1)
                );
            }
            openSitePct[t] = (double) p.numberOfOpenSites() / nSitesTotal;
        }

        mMean = StdStats.mean(openSitePct);
        mStddev = StdStats.stddev(openSitePct);

        final double conf95pBand = (1.96d * mStddev) / Math.sqrt(inTrials);
        mConfidenceLo = mMean - conf95pBand;
        mConfidenceHi = mMean + conf95pBand;    

    }


    //
    //  Public instance methods
    //

    /**
     *  @return sample mean of percolation threshold
     */
    public double mean() {
        return mMean;
    }

    /**
     *  @return sample standard deviation of percolation threshold
     */
    public double stddev() {
        return mStddev;
    }

    /**
     *  @return low endpoint of 95% confidence interval
     */
    public double confidenceLo() {
        return mConfidenceLo;
    }

    /**
     *  @return high endpoint of 95% confidence interval
     */
    public double confidenceHi() {
        return mConfidenceHi;
    }

}

