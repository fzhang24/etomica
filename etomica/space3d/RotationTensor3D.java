package etomica.space3d;

import etomica.api.IVector;

public class RotationTensor3D extends Tensor3D implements etomica.space.RotationTensor {
    public RotationTensor3D() {super(); reset();}
    public void reset() {
        xx = 1.0; xy = 0.0; xz = 0.0;
        yx = 0.0; yy = 1.0; yz = 0.0;
        zx = 0.0; zy = 0.0; zz = 1.0;
    }

    /**
     * Sets tensor for rotation about the indicated axis (0=x,1=y,2=z) by 
     * the given angle.
     */
    public void setAxial(int i, double theta) {
        double st = Math.sin(theta);
        double ct = Math.cos(theta);
        switch(i) {
            case 0: xx = 1.; xy = 0.; xz = 0.;
                    yx = 0.; yy = ct; yz = -st;
                    zx = 0.; zy = st; zz = ct;
                    return;
            case 1: xx = ct; xy = 0.; xz = -st;
                    yx = 0.; yy = 1.; yz = 0.;
                    zx = st; zy = 0.; zz = ct;
                    return;
            case 2: xx = ct; xy = -st; xz = 0.;
                    yx = st; yy = ct;  yz = 0.;
                    zx = 0.; zy = 0.;  zz = 1.;
                    return;
            default: throw new IllegalArgumentException("Improper axis specified for Space3D.RotationTensor.setAxial");
        }
    }
    /**
     * Not yet implemented.
     */
    public void setAngles(double[] angles) {
        throw new RuntimeException("Space3D.CoordinateGroup.setAngles() not yet implemented");
    }

    public void invert() {
        transpose();
    }

    public void setQuaternions(double[] q) {
        double q0 = q[0];
        double q1 = q[1];
        double q2 = q[2];
        double q3 = q[3];
        
        // A&T bogus
        xx = q0*q0 + q1*q1 - q2*q2 - q3*q3;
        xy = 2*(q1*q2 + q0*q3);
        xz = 2*(q1*q3 - q0*q2);
        
        yx = 2*(q1*q2 - q0*q3);
        yy = q0*q0 - q1*q1 + q2*q2 - q3*q3;
        yz = 2*(q2*q3 + q0*q1);
        
        zx = 2*(q1*q3 + q0*q2);
        zy = 2*(q2*q3 - q0*q1);
        zz = q0*q0 - q1*q1 - q2*q2 + q3*q3;

        // A&T smoke crack.  this is their matrix, transposed, and xy,yx,yz and zy are negatives of what is in the book.
//        xx = q0*q0 + q1*q1 - q2*q2 - q3*q3;
//        yx = -2*(q1*q2 + q0*q3);
//        zx = 2*(q1*q3 - q0*q2);
//        
//        xy = -2*(q1*q2 - q0*q3);
//        yy = q0*q0 - q1*q1 + q2*q2 - q3*q3;
//        zy = -2*(q2*q3 + q0*q1);
//        
//        xz = 2*(q1*q3 + q0*q2);
//        yz = -2*(q2*q3 - q0*q1);
//        zz = q0*q0 - q1*q1 - q2*q2 + q3*q3;
    }

    public void setOrientation(IOrientationFull3D orientation3D) {
        IVector direction = orientation3D.getDirection();
        IVector secondaryDirection = orientation3D.getSecondaryDirection();
        xx = direction.x(0);
        xy = direction.x(1);
        xz = direction.x(2);
        yx = secondaryDirection.x(0);
        yy = secondaryDirection.x(1);
        yz = secondaryDirection.x(2);
        // sorry, really!  we'll put it back shortly
        direction.XE(secondaryDirection);
        zx = direction.x(0);
        zy = direction.x(1);
        zz = direction.x(2);
        direction.setX(0, xx);
        direction.setX(1, xy);
        direction.setX(2, xz);
    }
    
    private static final long serialVersionUID = 1L;

    /**
     * Method to test rotation tensor.
     */
    public static void main (String[] args) {
        
        Vector3D r1 = new Vector3D(2,2,3);
        System.out.println("r1_before" + r1.toString());
        Tensor3D tensor = new Tensor3D(new double[][] {{1,2,0},{1,1,2},{0,0,1}});
        RotationTensor3D tensor2 = new RotationTensor3D();
        tensor2.E(tensor);
        System.out.println("tensor2_before " + tensor2.xx + "  " +tensor2.xy +"  "+tensor2.xz +"  "+tensor2.yx +"  "+tensor2.yy +"  "+tensor2.yz +"  "+tensor2.zx +"  "+tensor2.zy +"  "+tensor2.zz); 
        System.out.println();
    
        tensor2.transform(r1);
        System.out.println("r1_transform(tensor2)" + r1.toString());
        tensor2.invert();
        System.out.println("tensor2_invert " + tensor2.xx + "  " +tensor2.xy +"  "+tensor2.xz +"  "+tensor2.yx +"  "+tensor2.yy +"  "+tensor2.yz +"  "+tensor2.zx +"  "+tensor2.zy +"  "+tensor2.zz); 
        //tensor2.setAxial(1, 2*Math.PI);
        //System.out.println("tensor2_rotate_360 " + tensor2.xx + "  " +tensor2.xy +"  "+tensor2.xz +"  "+tensor2.yx +"  "+tensor2.yy +"  "+tensor2.yz +"  "+tensor2.zx +"  "+tensor2.zy +"  "+tensor2.zz); 
        //System.out.println();
    
        //r1.transform(tensor2);
        //System.out.println("r1_afterInvert_andRotate360 " + r1.toString());
    }//end of main 
    
}
