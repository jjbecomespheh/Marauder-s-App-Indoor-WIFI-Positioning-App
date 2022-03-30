package com.example.mywifiapp2;

import java.util.List;

/**
 * Build basic neural network model to train data and make prediction*/
public class NeuralNetwork {

    Matrix weights_ih , weights_ho , bias_h , bias_o;
    double l_rate=0.01;

    /**
     *1. weights_ih 👉 The weights matrix for the input and hidden layer.
     *2. weights_ho 👉 The weights matrix for the hidden and output layer.
     *3. bias_h 👉 The bias matrix for the hidden layer.
     *4. bias_o 👉 The bias matrix for the output layer.
     *5. l_rate 👉 The learning rate, a hyper-parameter used to control the learning steps during optimization of weights.
     */

    public NeuralNetwork(int i,int h,int o) {
        weights_ih = new Matrix(h,i);
        weights_ho = new Matrix(o,h);

        bias_h= new Matrix(h,1);
        bias_o= new Matrix(o,1);
    }

    public List<Double> predict(double[] X)
    {
        Matrix input = Matrix.fromArray(X);
        Matrix hidden = Matrix.multiply(weights_ih, input); // (h*i) multiply (i,1) => (h*1)
        hidden.add(bias_h);
        hidden.sigmoid();

        Matrix output = Matrix.multiply(weights_ho,hidden); // (o*h) multiply (h*1) => (o*1)
        output.add(bias_o);
        output.sigmoid();

        return output.toArray();
    }

    public void train(double [] X,double [] Y)
    {
        /**
         * 1. Convert X into input array
         * 2. Multiply with weights_ih, add bias_h to get hidden array
         * 3. Apply activation function to hidden array
         * 4. Multiply hidden array with weights_ho to get predicted output
         * 5. Apply activation function to predicted output*/
        Matrix input = Matrix.fromArray(X); // (i*1)
        Matrix hidden = Matrix.multiply(weights_ih, input); // (h*i) multiply (i*1) => (h*1)
        hidden.add(bias_h); // (h*1) add (h*1) => (h*1)
        hidden.sigmoid();

        Matrix output = Matrix.multiply(weights_ho,hidden); // (o*h) multiply (h*1) => (o*1)
        output.add(bias_o); // (o*1) add (o*1) => (o*1)
        output.sigmoid();

        /**
         * 1. Convert Y into target array
         * 2. Subtract target with output we get previously to get error matrix
         * 3. Calculate gradients for back propagation using error, output, and learning rate*/
        Matrix target = Matrix.fromArray(Y); // (o*1)

        Matrix error = Matrix.subtract(target, output); // (o*1) subtract (o*1) => (o*1)
        Matrix gradient = output.dsigmoid(); // (o*1)
        gradient.multiply(error); // (o*1) * (o*1) => (o*1)
        gradient.multiply(l_rate); // (o*1)

        /**
         * Do back propagation
         * formula: change = (learningRate *delta *value) + (momentum * pastChange)*/
        Matrix hidden_T = Matrix.transpose(hidden); // (1*h)
        Matrix who_delta =  Matrix.multiply(gradient, hidden_T); // (o*1) multiply (1*h) => (o*h)

        weights_ho.add(who_delta); //(o*h) add (o*h) => (o*h)
        bias_o.add(gradient); // (o*1) add (o*1) => (o*1)

        Matrix who_T = Matrix.transpose(weights_ho); // (h*o)
        Matrix hidden_errors = Matrix.multiply(who_T, error); // (h*o) multiply (o*1) => (h*1)

        Matrix h_gradient = hidden.dsigmoid(); // (h*1)
        h_gradient.multiply(hidden_errors); // (h*1) * (h*1) => (h*1)
        h_gradient.multiply(l_rate); // (h*1)

        Matrix i_T = Matrix.transpose(input); // (1*i)
        Matrix wih_delta = Matrix.multiply(h_gradient, i_T); // (h*1) * (1*i) => (h*i)

        weights_ih.add(wih_delta); // (h*i) add (h*i) => (h*i)
        bias_h.add(h_gradient); // (h*1) add (h*1) => (h*1)

    }

    public void fit(double[][]X,double[][]Y,int epochs)
    {
        for(int i=0;i<epochs;i++)
        {
            int sampleN = (int)(Math.random() * X.length);
            this.train(X[sampleN], Y[sampleN]);
        }
    }
}
