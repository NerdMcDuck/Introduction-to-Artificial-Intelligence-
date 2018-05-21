/**
 * 
 */
package colorization;

import java.util.concurrent.ThreadLocalRandom;

/**
 * @author Ramon
 *
 */
public class Neuron {
	public double[] weights, hiddenWeights;
	private double[] inputs; // 9 inputs per neuron? Or iterative over the array and each iteration pass it 9 new inputs? 
	private double step_size = 0.4; //also known as alpha
	private double error = 0.0;

	/**
	 * @param n is the number of input, +1 for the bias
	 * @param inputs The data value inputs
	 */
	public Neuron(int n){
		this.inputs = new double[n];
		weights = new double[n];
		hiddenWeights = new double[2];

		//Give each input a random weight between 0 and 1

		for(int i = 0; i < weights.length; i++){
			double val = ThreadLocalRandom.current().nextDouble(0, 0.2);
			weights[i] = val;
		}

		for(int i = 0; i < hiddenWeights.length; i++){
			double hval = ThreadLocalRandom.current().nextDouble(0, 0.3);
			hiddenWeights[i] = hval;
		}
	}


	/**
	 * Uses supervised learning to train the neural network on the data
	 * @param inputs
	 * @param desiredOutput
	 * @return
	 */
	public double[] train(double[] inputs, double desiredOutput){ //output a weight 
		double[] result = weights; //0=R, 1=G, 2=B
		double input_sum = 0.0;
		

		for(int i=0; i<inputs.length; i++){
			input_sum += inputs[i]*weights[i];
		}

		//output_guess = Sigmoid(input_sum); //output_R = Sigmoid( input_R )
	
		hiddenUpdate(input_sum, desiredOutput);
		update(input_sum, error);


		return result; //weights

	}

	/**
	 * This updates the weight of a given node
	 * @param input_sum the grayscale inputs and the bias
	 * @param desiredOutput the value we're trying to get to 
	 * @param error the difference between the output_guess and the desiredOutput
	 */
	public void update(double input_sum, double error){ //Missing variables 
		//weights_R[i] = weights_R[i] - step_size * error_R * derivativeSigmoid( input_R ) * input[i]

		for(int i = 0; i< inputs.length; i++){
			weights[i] = weights[i] - step_size * error * derSigmoid( input_sum ) * inputs[i];
		}

	}

	public void hiddenUpdate(double input_sum, double desiredOutput){
		double sum = 0.0;
		double[] hidinputs = {1.0, input_sum};
		double output_guess = 0.0;


		for(int i = 0; i<hidinputs.length; i++){
			sum += hidinputs[i] * hiddenWeights[i];
		}

		output_guess = Sigmoid( sum );
		error = error(output_guess, desiredOutput);

		for(int i = 0; i<hiddenWeights.length; i++){
			hiddenWeights[i] = hiddenWeights[i] - step_size * error * derSigmoid( sum ) * hidinputs[i];
		}

	}

	/**
	 * Calculates the error of our training output
	 * @param guess  the predicted value
	 * @param actual value that should be outputted after training
	 * @return the calculated error
	 */
	public double error(double guess, double actual){ //total error is the sum of the squared error
		//error = Sigmoid(sum) - desired_output
		return (guess - actual); //error_r = output_R - desired_output_R
	}

	/**
	 * Assign the neuron its inputs
	 * @param inputs the data to assign the neuron
	 */
	public void setInputs(double[] inputs){
		for(int i = 0; i < inputs.length; i++){
			this.inputs[i] = inputs[i];
		}
	}

	/**
	 * The Sigmoid Activation function
	 * @param x
	 * @return the result of the function
	 */
	private double Sigmoid(double x){ //Enough for this assignment? 
		return (1.0 / (1.0 + Math.exp(-x)));
	}


	/**
	 * The derivative of the Sigmoid function
	 * @param x the input at k
	 * @return g'(in_k)
	 */
	private double derSigmoid(double x){
		// e^x / ((e^x + 1)^2)

		return ( Math.exp(x) / ( Math.pow((Math.exp(x) + 1),2) ) );
	}

}
