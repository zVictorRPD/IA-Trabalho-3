/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package AvaliacaoFilme;

import javax.swing.JOptionPane;
import java.text.DecimalFormat;
import net.sourceforge.jFuzzyLogic.FIS;
import net.sourceforge.jFuzzyLogic.FunctionBlock;
import net.sourceforge.jFuzzyLogic.Gpr;
import net.sourceforge.jFuzzyLogic.plot.JFuzzyChart;
import net.sourceforge.jFuzzyLogic.rule.Variable;

/**
 * Test parsing an FCL file
 * @author gizelle
 */
public class AvaliacaoFilme 
{
	public static void main(String[] args) throws Exception 
	{
		// Formatador
		DecimalFormat df = new DecimalFormat("0.00");

		// Carrega o arquivo .FCL
		String fileName = "CAMINHO_ABSOLUTO_PRO_ARQUIVO\\rating.fcl";
		FIS fis = FIS.load(fileName, true);
		
		if (fis == null) { // Erro de arquivo
			System.err.println("Arquivo inexistente: '" + fileName + "'");
			return;
		}

		// mostra o conjunto de regras
		FunctionBlock functionBlock = fis.getFunctionBlock(null);
		JFuzzyChart.get().chart(functionBlock);

		// escolhe entradas
		String nota = JOptionPane.showInputDialog("Atribua uma nota para o roteiro (0 a 10):");
		functionBlock.setVariable("script", Integer.parseInt(nota));

		nota = JOptionPane.showInputDialog("Atribua uma nota para a atuação (0 a 10):");
		functionBlock.setVariable("acting", Integer.parseInt(nota));

		nota = JOptionPane.showInputDialog("Atribua uma nota para a direção (0 a 10):");
		functionBlock.setVariable("direction", Integer.parseInt(nota));

		// Avalia as regras 
		functionBlock.evaluate();

		// Mostra o gráfico de conclusões
		Variable tip = functionBlock.getVariable("rating");
		JFuzzyChart.get().chart(tip, tip.getDefuzzifier(), true);
		Gpr.debug("ruim[roteiro]: " + functionBlock.getVariable("script").getMembership("ruim"));
		Gpr.debug("bom[roteiro]: " + functionBlock.getVariable("script").getMembership("bom"));
		Gpr.debug("ótimo[roteiro]: " + functionBlock.getVariable("script").getMembership("otimo"));
		Gpr.debug("pessíma[atuação]: " + functionBlock.getVariable("acting").getMembership("pessima"));
		Gpr.debug("normal[atuação]: " + functionBlock.getVariable("acting").getMembership("normal"));
		Gpr.debug("majestosa[atuação]: " + functionBlock.getVariable("acting").getMembership("majestosa"));
		Gpr.debug("pessíma[atuação]: " + functionBlock.getVariable("direction").getMembership("pessima"));
		Gpr.debug("normal[atuação]: " + functionBlock.getVariable("direction").getMembership("normal"));
		Gpr.debug("ótima[atuação]: " + functionBlock.getVariable("direction").getMembership("otima"));

		// Imprime as regras no console
		System.out.println(functionBlock);

		// Formatando a avaliação para duas casas decimais
		String texto = "A avaliação da obra será de " + df.format(functionBlock.getVariable("rating").getValue()) + "";
		JOptionPane.showMessageDialog(null, texto);           
	}
}