from flask import Flask, request, jsonify
from summa import summarizer
from pdfminer.high_level import extract_text
import os

app = Flask(__name__)

def save_temp_file(file, filename='temp.pdf'):
    file.save(filename)
    return filename

def extract_pdf_text(file_path):
    try:
        return extract_text(file_path)
    except Exception as e:
        raise ValueError(f"Erro ao extrair texto do PDF: {str(e)}")

def generate_summary(text, ratio):
    if not text:
        raise ValueError("Nenhum texto extraído do PDF")
    
    return summarizer.summarize(text, ratio=ratio)

def validate_and_extract_params(request):
    if 'file' not in request.files:
        raise ValueError("Nenhum arquivo foi fornecido")
    
    file = request.files['file']
    
    if file.filename == '':
        raise ValueError("Nenhum arquivo selecionado")
    
    if not file.filename.endswith('.pdf'):
        raise ValueError("O arquivo não é um PDF")

    ratio = request.form.get('ratio', 0.2)
    try:
        ratio = float(ratio)
        if not 0 < ratio <= 1:
            raise ValueError("O ratio deve estar entre 0 e 1")
    except ValueError:
        raise ValueError("O valor do ratio deve ser um número válido entre 0 e 1")

    return file, ratio

@app.route('/summarize', methods=['POST'])
def summarize():
    try:
        file, ratio = validate_and_extract_params(request)

        temp_file_path = save_temp_file(file)

        text = extract_pdf_text(temp_file_path)

        summary = generate_summary(text, ratio)

        os.remove(temp_file_path)

        return jsonify({'summary': summary}), 200

    except ValueError as ve:
        return jsonify({'error': str(ve)}), 400
    except Exception as e:
        return jsonify({'error': str(e)}), 500

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5000)
